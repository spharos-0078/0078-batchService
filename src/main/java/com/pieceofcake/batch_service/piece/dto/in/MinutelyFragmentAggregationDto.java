package com.pieceofcake.batch_service.piece.dto.in;

import com.pieceofcake.batch_service.piece.entity.MinutelyFragmentAggregation;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MinutelyFragmentAggregationDto {
    private String pieceProductUuid;
    private Integer quantity;
    private Long averagePrice;
    private Long startingPrice;
    private Long closingPrice;
    private Long maximumPrice;
    private Long minimumPrice;
    private LocalDateTime date;

    @Builder
    public MinutelyFragmentAggregationDto(
            String pieceProductUuid,
            Integer quantity,
            Long averagePrice,
            Long startingPrice,
            Long closingPrice,
            Long maximumPrice,
            Long minimumPrice,
            LocalDateTime date
    ){
        this.pieceProductUuid = pieceProductUuid;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
        this.startingPrice = startingPrice;
        this.closingPrice = closingPrice;
        this.maximumPrice = maximumPrice;
        this.minimumPrice = minimumPrice;
        this.date = date;
    }

    public MinutelyFragmentAggregation toEntity(){
        return MinutelyFragmentAggregation.builder()
                .pieceProductUuid(pieceProductUuid)
                .quantity(quantity)
                .averagePrice(averagePrice)
                .startingPrice(startingPrice)
                .closingPrice(closingPrice)
                .maximumPrice(maximumPrice)
                .minimumPrice(minimumPrice)
                .date(date)
                .build();
    }
}
