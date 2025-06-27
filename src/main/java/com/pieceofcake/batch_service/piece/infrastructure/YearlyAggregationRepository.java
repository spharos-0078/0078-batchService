package com.pieceofcake.batch_service.piece.infrastructure;

import com.pieceofcake.batch_service.piece.entity.YearlyFragmentPriceAggregation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YearlyAggregationRepository extends JpaRepository<YearlyFragmentPriceAggregation, Long> {
    List<YearlyFragmentPriceAggregation> findTop50ByPieceProductUuidOrderByYearDesc(String pieceProductUuid);
}
