package ac.mju.memoria.backend.domain.diarybook.entity;

import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.EmotionWeather;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DiaryBookStatistics {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private YearMonth targetMonth; // 통계 대상 월

  private String oneLineSummary; // 한 줄 요약

  @Lob
  private String longSummary; // 장문 요약

  @Enumerated(EnumType.STRING)
  private EmotionWeather emotionWeather; // 감정 날씨

  @Lob // 이유가 길어질 수 있으므로 @Lob 추가
  private String emotionWeatherReason; // 감정 날씨 선택 이유 (새로 추가)

  @ElementCollection
  private List<UserRanking> attendanceRanking; // 사용자 출석 랭킹 (UserRanking으로 변경)

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "diary_book_id")
  private DiaryBook diaryBook;

  @Builder
  public DiaryBookStatistics(YearMonth targetMonth, String oneLineSummary, String longSummary,
      EmotionWeather emotionWeather, String emotionWeatherReason, List<UserRanking> attendanceRanking,
      DiaryBook diaryBook) {
    this.targetMonth = targetMonth;
    this.oneLineSummary = oneLineSummary;
    this.longSummary = longSummary;
    this.emotionWeather = emotionWeather;
    this.emotionWeatherReason = emotionWeatherReason;
    this.attendanceRanking = attendanceRanking;
    this.diaryBook = diaryBook;
  }

  public void updateStatistics(String oneLineSummary, String longSummary, EmotionWeather emotionWeather,
      String emotionWeatherReason,
      List<UserRanking> attendanceRanking) {
    this.oneLineSummary = oneLineSummary;
    this.longSummary = longSummary;
    this.emotionWeather = emotionWeather;
    this.emotionWeatherReason = emotionWeatherReason;
    this.attendanceRanking = attendanceRanking;
  }

  @Embeddable
  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  public static class DiaryRanking {
    private Long diaryId; // 원래대로 Long 타입
    private String diaryTitle;
    private Long count;

    @Builder
    public DiaryRanking(Long diaryId, String diaryTitle, Long count) {
      this.diaryId = diaryId;
      this.diaryTitle = diaryTitle;
      this.count = count;
    }
  }

  @Embeddable
  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  public static class UserRanking { // 사용자 기반 랭킹 (새로 추가)
    private String userId; // 사용자 이메일
    private String userNickname;
    private Long diaryCount; // 작성한 일기 수

    @Builder
    public UserRanking(String userId, String userNickname, Long diaryCount) {
      this.userId = userId;
      this.userNickname = userNickname;
      this.diaryCount = diaryCount;
    }
  }
}