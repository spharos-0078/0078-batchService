package com.pieceofcake.batch_service.piece.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class HourlyFragmentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pieceProductUuid;
    private Long price;
    private Integer quantity;
    private LocalDate date;

    @Builder
    public HourlyFragmentHistory(
            Long id,
            String pieceProductUuid,
            Long price,
            Integer quantity,
            LocalDate date
    ) {
        this.id = id;
        this.pieceProductUuid = pieceProductUuid;
        this.price = price;
        this.quantity = quantity;
        this.date = date;
    }
}
