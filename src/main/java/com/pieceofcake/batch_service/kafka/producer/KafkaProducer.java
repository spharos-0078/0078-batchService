package com.pieceofcake.batch_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaProducer {
    private final KafkaTemplate<String, DailyTradePieceEvent> kafkaTemplate;

    public void sendPieceReadEvent(DailyTradePieceEvent dailyTradePieceEvent) {
        log.info("send daily-piece-trade event");
        CompletableFuture<SendResult<String, DailyTradePieceEvent>> future
                = kafkaTemplate.send("daily-piece-trade", dailyTradePieceEvent);
    }
}
