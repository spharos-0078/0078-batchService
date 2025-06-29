package com.pieceofcake.batch_service.piece.dto.in;

import com.pieceofcake.batch_service.piece.entity.BestPieceProductAggregation;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Getter
@NoArgsConstructor
public class BestPieceProductAggregationDto {
    private String pieceProductUuid;
    private Integer ranking;
    private LocalDate date;

    @Builder
    public BestPieceProductAggregationDto(String pieceProductUuid, Integer ranking, LocalDate date) {
        this.pieceProductUuid = pieceProductUuid;
        this.ranking = ranking;
        this.date = date;
    }

    public BestPieceProductAggregation toEntity() {
        return BestPieceProductAggregation.builder()
                .pieceProductUuid(pieceProductUuid)
                .ranking(ranking)
                .date(date)
                .build();
    }
}
