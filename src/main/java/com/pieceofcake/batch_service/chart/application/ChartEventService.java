package com.pieceofcake.batch_service.chart.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pieceofcake.batch_service.chart.dto.out.GetChartRealTimeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChartEventService implements MessageListener {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final Map<String, Sinks.Many<GetChartRealTimeResponseDto>> sinks = new ConcurrentHashMap<>();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            GetChartRealTimeResponseDto event = objectMapper.readValue(json, GetChartRealTimeResponseDto.class);

            String pieceProductUuid = event.getPieceProductUuid();
            Sinks.Many<GetChartRealTimeResponseDto> sink = sinks.get(pieceProductUuid);
            if (sink != null) {
                sink.tryEmitNext(event);
            }
        } catch (Exception e) {
            log.error("Error processing Redis message", e);
        }
    }
    public Flux<GetChartRealTimeResponseDto> getChartRealTime(String pieceProductUuid){

        Sinks.Many<GetChartRealTimeResponseDto> sink = sinks.computeIfAbsent(pieceProductUuid,
                k -> Sinks.many().multicast().onBackpressureBuffer());

        // 최초 접속 시 현재 최고가 정보도 전달
        retrieveAndSendCurrentRealtime(pieceProductUuid, sink);

        return sink.asFlux()
                .doFinally(signalType -> {
                    // 구독이 끝났을 때(완료, 취소, 에러 모두 포함)
                    sinks.remove(pieceProductUuid);
                });
    }

    // 최초의 SSE 연결 시 데이터 전달 메서드
    private void retrieveAndSendCurrentRealtime(String pieceProductUuid, Sinks.Many<GetChartRealTimeResponseDto> sink){
        try {
            String key = "pieceVolume:" + pieceProductUuid + ":" + LocalDate.now();
            System.out.println("key : " + key);
            String jsonData = redisTemplate.opsForList().index(key, -1);  // Redis 리스트에서 마지막 데이터 읽기

            System.out.println("jsonData : " + jsonData);
            if (jsonData != null) {
                GetChartRealTimeResponseDto event = objectMapper.readValue(jsonData, GetChartRealTimeResponseDto.class);
                System.out.println("event : " + event.toString());
                sink.tryEmitNext(event);  // Sink에 데이터 전달
            }
        } catch (Exception e) {
            log.error("Error retrieving current highest bid for auction: {}", pieceProductUuid, e);
        }
    }

}
