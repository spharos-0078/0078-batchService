package com.pieceofcake.batch_service.chart.application;

import com.pieceofcake.batch_service.chart.dto.out.GetChartResponseDto;
import com.pieceofcake.batch_service.common.entity.BaseResponseStatus;
import com.pieceofcake.batch_service.common.exception.BaseException;
import com.pieceofcake.batch_service.piece.infrastructure.DailyAggregationRepository;
import com.pieceofcake.batch_service.piece.infrastructure.MinutelyFragmentAggregationRepository;
import com.pieceofcake.batch_service.piece.infrastructure.MonthlyAggregationRepository;
import com.pieceofcake.batch_service.piece.infrastructure.YearlyAggregationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChartServiceImpl implements ChartService {

    private final MinutelyFragmentAggregationRepository minutelyRepository;
    private final DailyAggregationRepository dailyRepository;
    private final MonthlyAggregationRepository monthlyRepository;
    private final YearlyAggregationRepository yearlyRepository;

    @Override
    public List<GetChartResponseDto> getHistoryFragmentAggregation(String pieceProductUuid) {
        return minutelyRepository.findHistoryByPieceProductUuidAndDate(pieceProductUuid, LocalDate.now().toString())
                .stream().map(GetChartResponseDto::fromMinutely).toList();
    }

    @Override
    public List<GetChartResponseDto> getDailyFragmentAggregation(String pieceProductUuid) {
        return dailyRepository.findTop50ByPieceProductUuidOrderByDateDesc(pieceProductUuid)
                .stream().map(GetChartResponseDto::fromDaily).toList();
    }

    @Override
    public List<GetChartResponseDto> getMonthlyFragmentAggregation(String pieceProductUuid) {
        return monthlyRepository.findTop50ByPieceProductUuidOrderByStartDateDesc(pieceProductUuid)
                .stream().map(GetChartResponseDto::fromMonthly).toList();
    }

    @Override
    public List<GetChartResponseDto> getYearlyFragmentAggregation(String pieceProductUuid) {
        return yearlyRepository.findTop50ByPieceProductUuidOrderByYearDesc(pieceProductUuid)
                .stream().map(GetChartResponseDto::fromYearly).toList();
    }
}
