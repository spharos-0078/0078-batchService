package com.pieceofcake.batch_service.piece.infrastructure;

import com.pieceofcake.batch_service.piece.entity.BestPieceProductAggregation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BestPieceProductAggregationRepository extends JpaRepository<BestPieceProductAggregation, Long> {
    List<BestPieceProductAggregation> findByDateOrderByRankingAsc(LocalDate date);
}
