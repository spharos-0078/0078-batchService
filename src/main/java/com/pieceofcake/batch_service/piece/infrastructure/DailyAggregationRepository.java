package com.pieceofcake.batch_service.piece.infrastructure;

import com.pieceofcake.batch_service.piece.entity.DailyFragmentPriceAggregation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyAggregationRepository extends JpaRepository<DailyFragmentPriceAggregation,Long> {
}
