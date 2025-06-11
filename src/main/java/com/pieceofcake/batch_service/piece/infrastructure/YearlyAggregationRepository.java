package com.pieceofcake.batch_service.piece.infrastructure;

import com.pieceofcake.batch_service.piece.entity.YearlyFragmentPriceAggregation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YearlyAggregationRepository extends JpaRepository<YearlyFragmentPriceAggregation, Long> {
}
