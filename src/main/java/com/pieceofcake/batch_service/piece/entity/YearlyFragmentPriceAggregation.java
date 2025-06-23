package com.pieceofcake.batch_service.piece.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class YearlyFragmentPriceAggregation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pieceProductUuid;
    private Long startingPrice;
    private Long closingPrice;
    private Long maximumPrice;
    private Long minimumPrice;
    private Long averagePrice;
    private Long tradeQuantity;
    private int year;

    @Builder
    public YearlyFragmentPriceAggregation(
            Long id,
            String pieceProductUuid,
            Long startingPrice,
            Long closingPrice,
            Long maximumPrice,
            Long minimumPrice,
            Long averagePrice,
            Long tradeQuantity,
            int year
    ){
        this.id = id;
        this.pieceProductUuid = pieceProductUuid;
        this.startingPrice = startingPrice;
        this.closingPrice = closingPrice;
        this.maximumPrice = maximumPrice;
        this.minimumPrice = minimumPrice;
        this.averagePrice = averagePrice;
        this.tradeQuantity = tradeQuantity;
        this.year = year;
    }
}
