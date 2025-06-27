package com.pieceofcake.batch_service.chart.presentation;

import com.pieceofcake.batch_service.chart.application.ChartEventService;
import com.pieceofcake.batch_service.chart.application.ChartService;
import com.pieceofcake.batch_service.chart.dto.out.GetChartRealTimeResponseDto;
import com.pieceofcake.batch_service.chart.dto.out.GetChartResponseDto;
import com.pieceofcake.batch_service.chart.vo.GetFragmentAggregationResponseVo;
import com.pieceofcake.batch_service.common.entity.BaseResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RequestMapping("/api/v1/piece")
@RequiredArgsConstructor
@RestController
public class ChartController {

    private final ChartEventService chartEventService;
    private final ChartService chartService;

    @Operation(summary = "실시간 체결가(시장가) 조회 SSE")
    @GetMapping(value = "/graph/real-time/{pieceProductUuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<GetChartRealTimeResponseDto> getChartRealTime(@PathVariable String pieceProductUuid){
        return chartEventService.getChartRealTime(pieceProductUuid);
    }

    @Operation(summary = "일별 시장가 조회 API")
    @GetMapping("/graph/daily/{pieceProductUuid}")
    public BaseResponseEntity<List<GetFragmentAggregationResponseVo>> getDailyFragmentAggregation(@PathVariable String pieceProductUuid){
        return new  BaseResponseEntity<>(chartService.getDailyFragmentAggregation(pieceProductUuid).stream().map(GetChartResponseDto::toVo).toList());
    }

    @Operation(summary = "월별 시장가 조회 API")
    @GetMapping("/graph/monthly/{pieceProductUuid}")
    public BaseResponseEntity<List<GetFragmentAggregationResponseVo>> getMonthlyFragmentAggregation(@PathVariable String pieceProductUuid){
        return new  BaseResponseEntity<>(chartService.getMonthlyFragmentAggregation(pieceProductUuid).stream().map(GetChartResponseDto::toVo).toList());
    }

    @Operation(summary = "연도별 시장가 조회 API")
    @GetMapping("/graph/yearly/{pieceProductUuid}")
    public BaseResponseEntity<List<GetFragmentAggregationResponseVo>> getYearlyFragmentAggregation(@PathVariable String pieceProductUuid){
        return new  BaseResponseEntity<>(chartService.getYearlyFragmentAggregation(pieceProductUuid).stream().map(GetChartResponseDto::toVo).toList());
    }

}
