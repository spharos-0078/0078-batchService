package com.pieceofcake.batch_service.kafka.producer;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyTradePieceEvent {
    private String pieceProductUuid;
    private Long closingPrice;
    private Long tradeQuantity;
}
