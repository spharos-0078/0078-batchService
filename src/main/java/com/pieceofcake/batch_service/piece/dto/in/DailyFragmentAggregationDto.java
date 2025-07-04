package com.pieceofcake.batch_service.piece.dto.in;

import com.pieceofcake.batch_service.piece.entity.DailyFragmentPriceAggregation;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class DailyFragmentAggregationDto {

    private String pieceProductUuid;
    private Long startingPrice;
    private Long closingPrice;
    private Long minimumPrice;
    private Long maximumPrice;
    private Long averagePrice;
    private Long tradeQuantity;
    private LocalDate date;

    @Builder
    public DailyFragmentAggregationDto(
            String pieceProductUuid,
            Long startingPrice,
            Long closingPrice,
            Long minimumPrice,
            Long maximumPrice,
            Long averagePrice,
            Long tradeQuantity,
            LocalDate date
    ){
        this.pieceProductUuid = pieceProductUuid;
        this.startingPrice = startingPrice;
        this.closingPrice = closingPrice;
        this.minimumPrice = minimumPrice;
        this.maximumPrice = maximumPrice;
        this.averagePrice = averagePrice;
        this.tradeQuantity = tradeQuantity;
        this.date = date;
    }

    public DailyFragmentPriceAggregation toEntity(){
        return DailyFragmentPriceAggregation.builder()
                .pieceProductUuid(this.pieceProductUuid)
                .startingPrice(this.startingPrice)
                .closingPrice(this.closingPrice)
                .maximumPrice(this.maximumPrice)
                .minimumPrice(this.minimumPrice)
                .averagePrice(this.averagePrice)
                .tradeQuantity(this.tradeQuantity)
                .date(this.date)
                .build();
    }
}
