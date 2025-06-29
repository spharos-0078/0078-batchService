package com.pieceofcake.batch_service.money.dto.out;

import com.pieceofcake.batch_service.money.entity.DailyMemberAssetAggregation;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GetAssetPieChartResponseDto {
    private String category;
    private Long amount;
    private Double ratio;
    private LocalDateTime date;

    @Builder
    public GetAssetPieChartResponseDto(String category, Long amount, Double ratio, LocalDateTime date) {
        this.category = category;
        this.amount = amount;
        this.ratio = ratio;
        this.date = date;
    }

}
