package com.pieceofcake.batch_service.piece.dto.in;

import com.pieceofcake.batch_service.piece.entity.YearlyFragmentPriceAggregation;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class YearlyFragmentAggregationDto {
    private String pieceProductUuid;
    private Long startingPrice;
    private Long closingPrice;
    private Long minimumPrice;
    private Long maximumPrice;
    private Long averagePrice;
    private Long tradeQuantity;
    private int year;

    @Builder
    public YearlyFragmentAggregationDto(
            String pieceProductUuid,
            Long startingPrice,
            Long closingPrice,
            Long minimumPrice,
            Long maximumPrice,
            Long averagePrice,
            Long tradeQuantity,
            int year
    ){
        this.pieceProductUuid = pieceProductUuid;
        this.startingPrice = startingPrice;
        this.closingPrice = closingPrice;
        this.minimumPrice = minimumPrice;
        this.maximumPrice = maximumPrice;
        this.averagePrice = averagePrice;
        this.tradeQuantity = tradeQuantity;
        this.year = year;
    }

    public YearlyFragmentPriceAggregation toEntity(){
        return YearlyFragmentPriceAggregation.builder()
                .pieceProductUuid( this.pieceProductUuid )
                .startingPrice( this.startingPrice )
                .closingPrice( this.closingPrice )
                .maximumPrice( this.maximumPrice )
                .minimumPrice( this.minimumPrice )
                .averagePrice( this.averagePrice )
                .tradeQuantity( this.tradeQuantity )
                .year( this.year )
                .build();
    }
}
