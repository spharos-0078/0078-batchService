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
public class BestPieceProductAggregation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pieceProductUuid;
    private Integer ranking;
    private LocalDate date;

    @Builder
    public BestPieceProductAggregation(String pieceProductUuid, Integer ranking, LocalDate date) {
        this.pieceProductUuid = pieceProductUuid;
        this.ranking = ranking;
        this.date = date;
    }
}
