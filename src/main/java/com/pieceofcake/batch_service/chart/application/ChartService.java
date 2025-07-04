package com.pieceofcake.batch_service.chart.application;

import com.pieceofcake.batch_service.chart.dto.out.GetChartResponseDto;

import java.util.List;

public interface ChartService {
    List<GetChartResponseDto> getHistoryFragmentAggregation(String pieceProductUuid);
    List<GetChartResponseDto> getDailyFragmentAggregation(String pieceProductUuid);
    List<GetChartResponseDto> getMonthlyFragmentAggregation(String pieceProductUuid);
    List<GetChartResponseDto> getYearlyFragmentAggregation(String pieceProductUuid);
}
