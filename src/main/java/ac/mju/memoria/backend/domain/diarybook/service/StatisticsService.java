package ac.mju.memoria.backend.domain.diarybook.service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import ac.mju.memoria.backend.domain.ai.llm.model.AIEmotionWeatherResponse;
import ac.mju.memoria.backend.domain.ai.llm.model.AISummaryResponse;
import ac.mju.memoria.backend.domain.ai.llm.service.EmotionWeatherAnalyzer;
import ac.mju.memoria.backend.domain.ai.llm.service.SummaryGenerator;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.event.DiaryCreatedEvent;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookStatisticsDto;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBookStatistics;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.EmotionWeather;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookRepository;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookStatisticsQueryRepository;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookStatisticsRepository;
import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {
  private static final Logger log = LoggerFactory.getLogger(StatisticsService.class);
  private final DiaryRepository diaryRepository;
  private final DiaryBookRepository diaryBookRepository;
  private final DiaryBookStatisticsRepository diaryBookStatisticsRepository;
  private final DiaryBookStatisticsQueryRepository diaryBookStatisticsQueryRepository;
  private final SummaryGenerator summaryGenerator;
  private final EmotionWeatherAnalyzer emotionWeatherAnalyzer;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleDiaryCreatedEvent(DiaryCreatedEvent event) {
    log.info("Received DiaryCreatedEvent for diaryBookId: {} (after commit)", event.getDiaryBookId());
    try {
      refreshStatisticsForDiaryBook(event.getDiaryBookId());
    } catch (Exception e) {
      log.error("Error processing DiaryCreatedEvent for diaryBookId: {}. Error: {}", event.getDiaryBookId(),
          e.getMessage(), e);
    }
  }

  @Transactional
  public void updateDiaryBookStatistics(Long diaryBookId, YearMonth targetMonth) {
    DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
        .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

    List<Diary> diaries = diaryRepository.findByDiaryBookAndCreatedAtBetween(
        diaryBook,
        targetMonth.atDay(1).atStartOfDay(),
        targetMonth.atEndOfMonth().atTime(23, 59, 59));

    String oneLineSummary = "요약 정보가 없습니다.";
    String longSummary = "요약 정보가 없습니다.";
    EmotionWeather emotionWeather = EmotionWeather.SUNNY;
    String emotionWeatherReason = "감정 날씨 분석 정보가 없습니다.";

    if (!diaries.isEmpty()) {
      String diaryEntries = diaries.stream()
          .map(diary -> "제목: " + (diary.getTitle() != null ? diary.getTitle() : "(제목 없음)") + "\n내용: "
              + diary.getContent() + "\n")
          .collect(Collectors.joining("\n---\n"));
      String targetMonthStr = targetMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

      try {
        AISummaryResponse summaryResponse = summaryGenerator.generateSummaries(targetMonthStr, diaryEntries);
        oneLineSummary = summaryResponse.getOneLineSummary();
        longSummary = summaryResponse.getLongSummary();
      } catch (Exception e) {
        log.error("Error generating AI summary for diaryBookId: {} and month: {}. Error: {}", diaryBookId, targetMonth,
            e.getMessage());
      }

      try {
        String AVAILABLE_WEATHERS = Arrays.stream(EmotionWeather.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));

        AIEmotionWeatherResponse weatherResponse = emotionWeatherAnalyzer.analyzeEmotionWeather(targetMonthStr, diaryEntries, AVAILABLE_WEATHERS);
        emotionWeather = weatherResponse.getEmotionWeather();
        emotionWeatherReason = weatherResponse.getReason();
      } catch (Exception e) {
        log.error("Error analyzing AI emotion weather for diaryBookId: {} and month: {}. Error: {}", diaryBookId,
            targetMonth,
            e.getMessage());
      }
    }

    List<DiaryBookStatistics.UserRanking> attendanceRanking = calculateAttendanceRanking(diaries);

    DiaryBookStatistics statistics = diaryBookStatisticsRepository.findByDiaryBookAndTargetMonth(diaryBook, targetMonth)
        .orElseGet(() -> DiaryBookStatistics.builder()
            .diaryBook(diaryBook)
            .targetMonth(targetMonth)
            .build());

    statistics.updateStatistics(oneLineSummary, longSummary, emotionWeather, emotionWeatherReason, attendanceRanking);
    diaryBookStatisticsRepository.save(statistics);
  }

  public DiaryBookStatisticsDto.StatisticsResponse getDiaryBookStatistics(Long diaryBookId, YearMonth targetMonth) {
    DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
        .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

    DiaryBookStatistics statistics = diaryBookStatisticsRepository.findByDiaryBookAndTargetMonth(diaryBook, targetMonth)
        .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_ANALYSIS_NOT_FOUND));

    List<Diary> topCommentDiaries = diaryBookStatisticsQueryRepository.findTopDiariesByCommentCount(diaryBook);
    List<Diary> topReactionDiaries = diaryBookStatisticsQueryRepository.findTopDiariesByReactionCount(diaryBook);

    return DiaryBookStatisticsDto.StatisticsResponse.from(statistics, topCommentDiaries, topReactionDiaries);
  }

  private List<DiaryBookStatistics.UserRanking> calculateAttendanceRanking(List<Diary> diaries) {
    Map<User, Long> diaryCountByUser = diaries.stream()
        .collect(Collectors.groupingBy(Diary::getAuthor, Collectors.counting()));

    return diaryCountByUser.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .map(entry -> {
          User user = entry.getKey();
          Long count = entry.getValue();
          return DiaryBookStatistics.UserRanking.builder()
              .userId(user.getEmail())
              .userNickname(user.getNickName())
              .diaryCount(count)
              .build();
        })
        .collect(Collectors.toList());
  }

  @Transactional
  public void refreshStatisticsForDiaryBook(Long diaryBookId) {
    updateDiaryBookStatistics(diaryBookId, YearMonth.now());
  }

  private String getContentPreview(String content) {
    if (content == null || content.isEmpty()) {
      return "";
    }
    return content.substring(0, Math.min(content.length(), 20));
  }
}
