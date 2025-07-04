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
    private Long startingPrice;
    private Long closingPrice;
    private Long maximumPrice;
    private Long minimumPrice;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime matchedTime;
    private Integer matchedQuantity;

    @Builder
    public GetChartRealTimeResponseDto(
            String pieceProductUuid,
            Long piecePrice,
            Long startingPrice,
            Long closingPrice,
            Long maximumPrice,
            Long minimumPrice,
            LocalDateTime matchedTime,
            Integer matchedQuantity) {
        this.pieceProductUuid = pieceProductUuid;
        this.piecePrice = piecePrice;
        this.startingPrice = startingPrice;
        this.closingPrice = closingPrice;
        this.maximumPrice = maximumPrice;
        this.minimumPrice = minimumPrice;
        this.matchedTime = matchedTime;
        this.matchedQuantity = matchedQuantity;
    }
}
