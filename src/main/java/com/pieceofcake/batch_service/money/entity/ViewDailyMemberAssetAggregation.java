package com.pieceofcake.batch_service.money.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@Entity
@Getter
@Immutable
@Table(name = "member_money_summary")
public class ViewDailyMemberAssetAggregation {
    @Id
    private String memberUuid;
    private Long totalFunding;
    private Long totalPiece;
    private Long totalMoney;
    private Long totalBid;
}
