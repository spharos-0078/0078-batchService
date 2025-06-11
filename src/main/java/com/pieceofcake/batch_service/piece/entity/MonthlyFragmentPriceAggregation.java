package com.pieceofcake.batch_service.piece.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class MonthlyFragmentPriceAggregation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pieceProductUuid;
    private Double startingPrice;
    private Double closingPrice;
    private Double maximumPrice;
    private Double minimumPrice;
    private Double averagePrice;
    private Long tradeQuantity;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    public MonthlyFragmentPriceAggregation(
            Long id,
            String pieceProductUuid,
            Double startingPrice,
            Double closingPrice,
            Double maximumPrice,
            Double minimumPrice,
            Double averagePrice,
            Long tradeQuantity,
            LocalDate startDate,
            LocalDate endDate) {

        this.id = id;
        this.pieceProductUuid = pieceProductUuid;
        this.startingPrice = startingPrice;
        this.closingPrice = closingPrice;
        this.maximumPrice = maximumPrice;
        this.minimumPrice = minimumPrice;
        this.averagePrice = averagePrice;
        this.tradeQuantity = tradeQuantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
