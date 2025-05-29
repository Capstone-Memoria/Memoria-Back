package ac.mju.memoria.backend.domain.diarybook.controller;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookStatisticsDto;
import ac.mju.memoria.backend.domain.diarybook.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "일기장 통계", description = "일기장 통계 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {

  private final StatisticsService statisticsService;

  @Operation(summary = "일기장 월별 통계 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "일기장 월별 통계 조회 성공", content = @Content(schema = @Schema(implementation = DiaryBookStatisticsDto.StatisticsResponse.class))),
      @ApiResponse(responseCode = "404", description = "해당 일기장 또는 통계 정보를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true)))
  })
  @GetMapping("/diary-book/{diaryBookId}")
  public ResponseEntity<DiaryBookStatisticsDto.StatisticsResponse> getDiaryBookStatistics(
      @Parameter(description = "일기장 ID", required = true, example = "1") @PathVariable Long diaryBookId,
      @Parameter(description = "조회할 월 (yyyy-MM 형식)", required = true, example = "2024-07") @RequestParam String month // yyyy-MM
                                                                                                                      // 형식
  ) {
    YearMonth targetMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
    DiaryBookStatisticsDto.StatisticsResponse response = statisticsService.getDiaryBookStatistics(diaryBookId,
        targetMonth);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "일기장 현재 월 통계 수동 업데이트")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "일기장 현재 월 통계 수동 업데이트 성공"),
      @ApiResponse(responseCode = "404", description = "해당 일기장을 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true)))
  })
  @PostMapping("/diary-book/{diaryBookId}/refresh")
  public ResponseEntity<Void> refreshDiaryBookStatistics(
      @Parameter(description = "일기장 ID", required = true, example = "1") @PathVariable Long diaryBookId) {
    statisticsService.refreshStatisticsForDiaryBook(diaryBookId);
    return ResponseEntity.ok().build();
  }
}
