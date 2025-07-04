package com.pieceofcake.batch_service.chart.presentation;

import com.pieceofcake.batch_service.chart.application.ChartService;
import com.pieceofcake.batch_service.chart.dto.out.GetChartResponseDto;
import com.pieceofcake.batch_service.chart.vo.GetFragmentAggregationResponseVo;
import com.pieceofcake.batch_service.common.entity.BaseResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/piece")
@RequiredArgsConstructor
@RestController
public class ChartController {

    private final ChartService chartService;

    @Operation(summary = "오늘 기준 과거 시장가 조회 API", description = "특정 조각 상품의 과거 시장가(분 단위)를 조회합니다.")
    @GetMapping("/graph/history/{pieceProductUuid}")
    public BaseResponseEntity<List<GetFragmentAggregationResponseVo>> getMinutelyFragmentAggregation(
            @Parameter(description = "조각 상품의 UUID")
            @PathVariable String pieceProductUuid){
        return new  BaseResponseEntity<>(chartService.getHistoryFragmentAggregation(pieceProductUuid).stream().map(GetChartResponseDto::toVo).toList());
    }

    @Operation(summary = "일별 시장가 조회 API", description = "특정 조각 상품의 일별 시장가를 조회합니다.")
    @GetMapping("/graph/daily/{pieceProductUuid}")
    public BaseResponseEntity<List<GetFragmentAggregationResponseVo>> getDailyFragmentAggregation(
            @Parameter(description = "조각 상품의 UUID")
            @PathVariable String pieceProductUuid){
        return new  BaseResponseEntity<>(chartService.getDailyFragmentAggregation(pieceProductUuid).stream().map(GetChartResponseDto::toVo).toList());
    }

    @Operation(summary = "월별 시장가 조회 API", description = "특정 조각 상품의 월별 시장가를 조회합니다.")
    @GetMapping("/graph/monthly/{pieceProductUuid}")
    public BaseResponseEntity<List<GetFragmentAggregationResponseVo>> getMonthlyFragmentAggregation(
            @Parameter(description = "조각 상품의 UUID")
            @PathVariable String pieceProductUuid){
        return new  BaseResponseEntity<>(chartService.getMonthlyFragmentAggregation(pieceProductUuid).stream().map(GetChartResponseDto::toVo).toList());
    }

    @Operation(summary = "연도별 시장가 조회 API", description = "특정 조각 상품의 연도별 시장가를 조회합니다.")
    @GetMapping("/graph/yearly/{pieceProductUuid}")
    public BaseResponseEntity<List<GetFragmentAggregationResponseVo>> getYearlyFragmentAggregation(
            @Parameter(description = "조각 상품의 UUID")
            @PathVariable String pieceProductUuid){
        return new  BaseResponseEntity<>(chartService.getYearlyFragmentAggregation(pieceProductUuid).stream().map(GetChartResponseDto::toVo).toList());
    }
}
