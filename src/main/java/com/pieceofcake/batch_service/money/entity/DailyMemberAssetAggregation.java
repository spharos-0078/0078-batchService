package com.pieceofcake.batch_service.money.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class DailyMemberAssetAggregation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String memberUuid;

    @Column(nullable = false)
    private Long totalMoney;

    @Column(nullable = false)
    private Long totalBid;

    @Column(nullable = false)
    private Long totalPiece;

    @Column(nullable = false)
    private Long totalFunding;

    private LocalDateTime date;

    @Builder
    public DailyMemberAssetAggregation(
            String memberUuid,
            Long totalMoney,
            Long totalBid,
            Long totalPiece,
            Long totalFunding,
            LocalDateTime date
    ) {
        this.memberUuid = memberUuid;
        this.totalMoney = totalMoney;
        this.totalBid = totalBid;
        this.totalPiece = totalPiece;
        this.totalFunding = totalFunding;
        this.date = date;
    }
}
