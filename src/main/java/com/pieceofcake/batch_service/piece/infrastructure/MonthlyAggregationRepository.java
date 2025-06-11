package com.pieceofcake.batch_service.piece.infrastructure;

import com.pieceofcake.batch_service.piece.entity.MonthlyFragmentPriceAggregation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyAggregationRepository extends JpaRepository<MonthlyFragmentPriceAggregation, Long> {
}
