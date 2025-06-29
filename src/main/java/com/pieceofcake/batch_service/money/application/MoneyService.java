package com.pieceofcake.batch_service.money.application;

import com.pieceofcake.batch_service.money.dto.out.GetAssetPieChartResponseDto;

import java.util.List;

public interface MoneyService {
    List<GetAssetPieChartResponseDto> getMyAssetPieChart(String memberUuid);
}
