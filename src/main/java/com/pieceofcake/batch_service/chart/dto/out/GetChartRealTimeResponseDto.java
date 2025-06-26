package com.pieceofcake.batch_service.chart.dto.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class GetChartRealTimeResponseDto {
    private String pieceProductUuid;
    private Long piecePrice;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime matchedTime;
    private Integer matchedQuantity;

    @Builder
    public GetChartRealTimeResponseDto(String pieceProductUuid, Long piecePrice, LocalDateTime matchedTime, Integer matchedQuantity) {
        this.pieceProductUuid = pieceProductUuid;
        this.piecePrice = piecePrice;
        this.matchedTime = matchedTime;
        this.matchedQuantity = matchedQuantity;
    }
}
