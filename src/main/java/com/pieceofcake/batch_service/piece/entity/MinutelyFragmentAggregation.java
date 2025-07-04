package com.pieceofcake.batch_service.piece.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@NoArgsConstructor
@Entity
public class MinutelyFragmentAggregation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pieceProductUuid;
    private Long averagePrice;
    private Long startingPrice;
    private Long closingPrice;
    private Long maximumPrice;
    private Long minimumPrice;
    private Integer quantity;
    private LocalDateTime date;

    @Builder
    public MinutelyFragmentAggregation(
            Long id,
            String pieceProductUuid,
            Long averagePrice,
            Long startingPrice,
            Long closingPrice,
            Long maximumPrice,
            Long minimumPrice,
            Integer quantity,
            LocalDateTime date
    ) {
        this.id = id;
        this.pieceProductUuid = pieceProductUuid;
        this.averagePrice = averagePrice;
        this.startingPrice = startingPrice;
        this.closingPrice = closingPrice;
        this.maximumPrice = maximumPrice;
        this.minimumPrice = minimumPrice;
        this.quantity = quantity;
        this.date = date;
    }
}
