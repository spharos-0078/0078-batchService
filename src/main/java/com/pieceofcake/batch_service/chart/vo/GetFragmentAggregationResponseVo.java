package com.pieceofcake.batch_service.chart.vo;

import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GetFragmentAggregationResponseVo {
    private String pieceProductUuid;
    private Long startingPrice;
    private Long closingPrice;
    private Long minimumPrice;
    private Long maximumPrice;
    private Long averagePrice;
    private Long tradeQuantity;
    private LocalDate date;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer year;
}
