package com.pieceofcake.batch_service.money.presentation;

import com.pieceofcake.batch_service.money.application.MoneyService;
import com.pieceofcake.batch_service.money.dto.out.GetAssetPieChartResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/money")
@RequiredArgsConstructor
public class MoneyController {
    private final MoneyService moneyService;

    @Operation(summary = "내자산 원형차트 API")
    @GetMapping
    public List<GetAssetPieChartResponseDto> getMyAssetPieChart(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid
    ) {
        return moneyService.getMyAssetPieChart(memberUuid);
    }
}