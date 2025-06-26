package com.pieceofcake.batch_service.chart.presentation;

import com.pieceofcake.batch_service.chart.application.ChartEventService;
import com.pieceofcake.batch_service.chart.dto.out.GetChartRealTimeResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RequestMapping("/api/v1/piece")
@RequiredArgsConstructor
@RestController
public class ChartController {

    private final ChartEventService chartEventService;

    @Operation(summary = "실시간 체결가(시장가) 조회 SSE")
    @GetMapping(value = "/graph/real-time/{pieceProductUuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<GetChartRealTimeResponseDto> getChartRealTime(@PathVariable String pieceProductUuid){
        return chartEventService.getChartRealTime(pieceProductUuid);
    }
}
