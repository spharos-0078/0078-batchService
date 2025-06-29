package com.pieceofcake.batch_service.money.infrastructure;

import com.pieceofcake.batch_service.money.entity.DailyMemberAssetAggregation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DailyMemberAssetAggregationRepository extends JpaRepository<DailyMemberAssetAggregation,Long> {
    Optional<DailyMemberAssetAggregation> findByMemberUuid(String memberUuid);
}
