package com.pieceofcake.batch_service.money.application;

import com.pieceofcake.batch_service.common.entity.BaseResponseStatus;
import com.pieceofcake.batch_service.common.exception.BaseException;
import com.pieceofcake.batch_service.money.dto.out.GetAssetPieChartResponseDto;
import com.pieceofcake.batch_service.money.entity.DailyMemberAssetAggregation;
import com.pieceofcake.batch_service.money.infrastructure.DailyMemberAssetAggregationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MoneyServiceImpl implements MoneyService {

    private final DailyMemberAssetAggregationRepository repository;

    @Override
    public List<GetAssetPieChartResponseDto> getMyAssetPieChart(String memberUuid) {

        DailyMemberAssetAggregation entity = repository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NO_MY_CHART));

        long total = entity.getTotalMoney() + entity.getTotalBid() + entity.getTotalPiece() + entity.getTotalFunding();
        if (total == 0) {
            return Collections.emptyList(); // 또는 비율 0으로 반환
        }

        List<GetAssetPieChartResponseDto> result = new ArrayList<>();

        result.add(GetAssetPieChartResponseDto.builder()
                .category("예치금")
                .amount(entity.getTotalMoney())
                .ratio(calcRatio(entity.getTotalMoney(), total))
                .date(entity.getDate())
                .build());

        result.add(GetAssetPieChartResponseDto.builder()
                .category("보증금")
                .amount(entity.getTotalBid())
                .ratio(calcRatio(entity.getTotalBid(), total))
                .date(entity.getDate())
                .build());

        result.add(GetAssetPieChartResponseDto.builder()
                .category("조각")
                .amount(entity.getTotalPiece())
                .ratio(calcRatio(entity.getTotalPiece(), total))
                .date(entity.getDate())
                .build());

        result.add(GetAssetPieChartResponseDto.builder()
                .category("공모")
                .amount(entity.getTotalFunding())
                .ratio(calcRatio(entity.getTotalFunding(), total))
                .date(entity.getDate())
                .build());

        return result;
    }

    private double calcRatio(long value, long total) {
        return Math.round((value * 10000.0 / total)) / 100.0; // 소수점 두 자리까지
    }
}
