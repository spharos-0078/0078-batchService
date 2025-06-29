package com.pieceofcake.batch_service.piece.dto.out;

import com.pieceofcake.batch_service.piece.entity.BestPieceProductAggregation;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class GetBestPieceProductAggregationDto {
    private String pieceProductUuid;
    private Integer ranking;
    private LocalDate date;

    @Builder
    public GetBestPieceProductAggregationDto(String pieceProductUuid, Integer ranking, LocalDate date) {
        this.pieceProductUuid = pieceProductUuid;
        this.ranking = ranking;
        this.date = date;
    }

    public GetBestPieceProductAggregationDto from(BestPieceProductAggregation aggregation){
        return GetBestPieceProductAggregationDto.builder()
                .pieceProductUuid(aggregation.getPieceProductUuid())
                .ranking(aggregation.getRanking())
                .date(aggregation.getDate())
                .build();
    }
}
