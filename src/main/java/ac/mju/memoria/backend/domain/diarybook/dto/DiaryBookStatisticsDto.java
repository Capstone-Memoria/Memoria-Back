package ac.mju.memoria.backend.domain.diarybook.dto;

import java.util.List;
import java.util.stream.Collectors;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBookStatistics;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.EmotionWeather;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiaryBookStatisticsDto {

  @Getter
  @Builder
  @Schema(description = "일기장 통계 응답 DTO")
  public static class StatisticsResponse {

    @Schema(description = "통계 대상 월 (yyyy-MM 형식)", example = "2024-07")
    private String targetMonth;

    @Schema(description = "한 줄 요약", example = "이번 달은 행복한 한 달이었어요!")
    private String oneLineSummary;

    @Schema(description = "상세 요약", example = "친구들과의 여행, 새로운 프로젝트 시작 등 즐거운 일이 많았고, 가끔 힘들 때도 있었지만 잘 극복해냈습니다.")
    private String longSummary;

    @Schema(description = "감정 날씨", example = "SUNNY")
    private EmotionWeather emotionWeather;

    @Schema(description = "감정 날씨 분석 이유", example = "전반적으로 긍정적인 단어 사용 빈도가 높았으며, 특히 '행복', '즐거움' 등의 단어가 자주 등장했습니다.")
    private String emotionWeatherReason;

    @Schema(description = "출석 랭킹")
    private List<UserRankingResponse> attendanceRanking;

    @Schema(description = "댓글 랭킹")
    private List<DiaryRankingResponse> commentRanking;

    @Schema(description = "반응 랭킹")
    private List<DiaryRankingResponse> reactionRanking;

    public static StatisticsResponse from(DiaryBookStatistics statistics,
        List<ac.mju.memoria.backend.domain.diary.entity.Diary> topCommentDiaries,
        List<ac.mju.memoria.backend.domain.diary.entity.Diary> topReactionDiaries) {
      return StatisticsResponse.builder()
          .targetMonth(statistics.getTargetMonth().toString())
          .oneLineSummary(statistics.getOneLineSummary())
          .longSummary(statistics.getLongSummary())
          .emotionWeather(statistics.getEmotionWeather())
          .emotionWeatherReason(statistics.getEmotionWeatherReason())
          .attendanceRanking(statistics.getAttendanceRanking().stream()
              .map(UserRankingResponse::from)
              .collect(Collectors.toList()))
          .commentRanking(topCommentDiaries.stream()
              .map(diary -> DiaryRankingResponse.from(diary, (long) diary.getComments().size()))
              .collect(Collectors.toList()))
          .reactionRanking(topReactionDiaries.stream()
              .map(diary -> DiaryRankingResponse.from(diary, (long) diary.getReactions().size()))
              .collect(Collectors.toList()))
          .build();
    }
  }

  @Getter
  @Builder
  @Schema(description = "사용자 랭킹 응답 DTO")
  public static class UserRankingResponse {

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String userId;

    @Schema(description = "사용자 닉네임", example = "행복한쿼카")
    private String userNickname;

    @Schema(description = "일기 작성 수", example = "15")
    private Long diaryCount;

    public static UserRankingResponse from(DiaryBookStatistics.UserRanking userRanking) {
      return UserRankingResponse.builder()
          .userId(userRanking.getUserId())
          .userNickname(userRanking.getUserNickname())
          .diaryCount(userRanking.getDiaryCount())
          .build();
    }
  }

  @Getter
  @Builder
  @Schema(description = "일기 랭킹 응답 DTO")
  public static class DiaryRankingResponse {

    @Schema(description = "일기 ID", example = "1")
    private Long diaryId;

    @Schema(description = "일기 제목 또는 내용 미리보기", example = "오늘의 일기")
    private String diaryTitle;

    @Schema(description = "댓글 또는 반응 수", example = "10")
    private Long count;

    public static DiaryRankingResponse from(ac.mju.memoria.backend.domain.diary.entity.Diary diary, Long count) {
      return DiaryRankingResponse.builder()
          .diaryId(diary.getId())
          .diaryTitle(diary.getTitle() != null ? diary.getTitle() : getContentPreview(diary.getContent()))
          .count(count)
          .build();
    }

    private static String getContentPreview(String content) {
      if (content == null || content.isEmpty()) {
        return "";
      }
      return content.substring(0, Math.min(content.length(), 20));
    }
  }
}