package com.pieceofcake.batch_service.piece.infrastructure;

import com.pieceofcake.batch_service.piece.entity.MinutelyFragmentAggregation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MinutelyFragmentAggregationRepository extends JpaRepository<MinutelyFragmentAggregation,Long> {
    @Query(value = "SELECT * FROM minutely_fragment_aggregation WHERE piece_product_uuid = :pieceProductUuid " +
            "AND DATE(date) = :targetDate ORDER BY id ASC", nativeQuery = true)
    List<MinutelyFragmentAggregation> findHistoryByPieceProductUuidAndDate(
            @Param("pieceProductUuid") String pieceProductUuid,
            @Param("targetDate") String targetDate);
}
