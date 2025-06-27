package com.pieceofcake.batch_service.piece.infrastructure;

import com.pieceofcake.batch_service.piece.entity.MonthlyFragmentPriceAggregation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonthlyAggregationRepository extends JpaRepository<MonthlyFragmentPriceAggregation, Long> {
    List<MonthlyFragmentPriceAggregation> findTop50ByPieceProductUuidOrderByStartDateDesc(String pieceProductUuid);
}
