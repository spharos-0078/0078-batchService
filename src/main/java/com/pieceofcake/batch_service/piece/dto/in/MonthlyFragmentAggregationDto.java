package com.pieceofcake.batch_service.piece.dto.in;

import com.pieceofcake.batch_service.piece.entity.MonthlyFragmentPriceAggregation;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Getter
@NoArgsConstructor
public class MonthlyFragmentAggregationDto {
    private String pieceProductUuid;
    private Double startingPrice;
    private Double closingPrice;
    private Double minimumPrice;
    private Double maximumPrice;
    private Double averagePrice;
    private Long tradeQuantity;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    public MonthlyFragmentAggregationDto(
                String pieceProductUuid,
                Double startingPrice,
                Double closingPrice,
                Double minimumPrice,
                Double maximumPrice,
                Double averagePrice,
                Long tradeQuantity,
                LocalDate startDate,
                LocalDate endDate){
        this.pieceProductUuid = pieceProductUuid;
        this.startingPrice = startingPrice;
        this.closingPrice = closingPrice;
        this.minimumPrice = minimumPrice;
        this.maximumPrice = maximumPrice;
        this.averagePrice = averagePrice;
        this.tradeQuantity = tradeQuantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public MonthlyFragmentPriceAggregation toEntity(){
        return MonthlyFragmentPriceAggregation.builder()
                .pieceProductUuid(this.pieceProductUuid)
                .startingPrice(this.startingPrice)
                .closingPrice(this.closingPrice)
                .maximumPrice(this.maximumPrice)
                .minimumPrice(this.minimumPrice)
                .averagePrice(this.averagePrice)
                .tradeQuantity(this.tradeQuantity)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .build();
    }
}
