package com.pieceofcake.batch_service.money.presentation;

import com.pieceofcake.batch_service.common.entity.BaseResponseEntity;
import com.pieceofcake.batch_service.money.application.MoneyService;
import com.pieceofcake.batch_service.money.dto.out.GetAssetPieChartResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
        summary = "내자산 원형차트 API",
        description = "회원의 자산 분포를 원형 차트 형태로 조회합니다.\n" +
                "ratio는 전체 자산에서 해당 카테고리(예치금, 보증금, 조각 투자금, 공모투자금)가 차지하는 비율(%)입니다.\n" +
                "\n카테고리 설명:\n" +
                "- 예치금: 현재 사용 가능한 금액\n" +
                "- 보증금\n" +
                "- 조각 투자금\n" +
                "- 공모투자금"
    )
    @GetMapping
    public BaseResponseEntity<List<GetAssetPieChartResponseDto>> getMyAssetPieChart(
            @Parameter(description = "회원 UUID")
            @RequestHeader(value = "X-Member-Uuid") String memberUuid
    ) {
        return new BaseResponseEntity<>(moneyService.getMyAssetPieChart(memberUuid));
    }
}