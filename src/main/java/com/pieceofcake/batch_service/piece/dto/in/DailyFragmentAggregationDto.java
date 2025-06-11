package com.pieceofcake.batch_service.piece.dto.in;

import com.pieceofcake.batch_service.piece.entity.DailyFragmentPriceAggregation;
import com.pieceofcake.batch_service.piece.entity.HourlyFragmentHistory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class DailyFragmentAggregationDto {

    private String pieceProductUuid;
    private Double startingPrice;
    private Double closingPrice;
    private Double minimumPrice;
    private Double maximumPrice;
    private Double averagePrice;
    private Long tradeQuantity;
    private LocalDate date;

    @Builder
    public DailyFragmentAggregationDto(
            String pieceProductUuid,
            Double startingPrice,
            Double closingPrice,
            Double minimumPrice,
            Double maximumPrice,
            Double averagePrice,
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
