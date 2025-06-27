package com.pieceofcake.batch_service.chart.dto.out;

import com.pieceofcake.batch_service.chart.vo.GetFragmentAggregationResponseVo;
import com.pieceofcake.batch_service.piece.entity.DailyFragmentPriceAggregation;
import com.pieceofcake.batch_service.piece.entity.MonthlyFragmentPriceAggregation;
import com.pieceofcake.batch_service.piece.entity.YearlyFragmentPriceAggregation;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor
public class GetChartResponseDto {
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

    @Builder
    public GetChartResponseDto(
            String pieceProductUuid,
            Long startingPrice,
            Long closingPrice,
            Long minimumPrice,
            Long maximumPrice,
            Long averagePrice,
            Long tradeQuantity,
            LocalDate date,
            LocalDate startDate,
            LocalDate endDate,
            Integer year
    ){
        this.pieceProductUuid = pieceProductUuid;
        this.startingPrice = startingPrice;
        this.closingPrice = closingPrice;
        this.minimumPrice = minimumPrice;
        this.maximumPrice = maximumPrice;
        this.averagePrice = averagePrice;
        this.tradeQuantity = tradeQuantity;
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
        this.year = year;
    }

    public static GetChartResponseDto fromDaily(DailyFragmentPriceAggregation aggregation) {
        return GetChartResponseDto.builder()
                .pieceProductUuid(aggregation.getPieceProductUuid())
                .startingPrice(aggregation.getStartingPrice())
                .closingPrice(aggregation.getClosingPrice())
                .minimumPrice(aggregation.getMinimumPrice())
                .maximumPrice(aggregation.getMaximumPrice())
                .averagePrice(aggregation.getAveragePrice())
                .tradeQuantity(aggregation.getTradeQuantity())
                .date(aggregation.getDate())
                .build();
    }

    public static GetChartResponseDto fromMonthly(MonthlyFragmentPriceAggregation aggregation) {
        return GetChartResponseDto.builder()
                .pieceProductUuid(aggregation.getPieceProductUuid())
                .startingPrice(aggregation.getStartingPrice())
                .closingPrice(aggregation.getClosingPrice())
                .minimumPrice(aggregation.getMinimumPrice())
                .maximumPrice(aggregation.getMaximumPrice())
                .averagePrice(aggregation.getAveragePrice())
                .tradeQuantity(aggregation.getTradeQuantity())
                .startDate(aggregation.getStartDate())
                .endDate(aggregation.getEndDate())
                .build();
    }

    public static GetChartResponseDto fromYearly(YearlyFragmentPriceAggregation aggregation) {
        return GetChartResponseDto.builder()
                .pieceProductUuid(aggregation.getPieceProductUuid())
                .startingPrice(aggregation.getStartingPrice())
                .closingPrice(aggregation.getClosingPrice())
                .minimumPrice(aggregation.getMinimumPrice())
                .maximumPrice(aggregation.getMaximumPrice())
                .averagePrice(aggregation.getAveragePrice())
                .tradeQuantity(aggregation.getTradeQuantity())
                .year(aggregation.getYear())
                .build();
    }

    public GetFragmentAggregationResponseVo toVo(){
        return GetFragmentAggregationResponseVo.builder()
                .pieceProductUuid(this.pieceProductUuid)
                .startingPrice(this.startingPrice)
                .closingPrice(this.closingPrice)
                .minimumPrice(this.minimumPrice)
                .maximumPrice(this.maximumPrice)
                .averagePrice(this.averagePrice)
                .tradeQuantity(this.tradeQuantity)
                .date(this.date)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .year(this.year)
                .build();
    }
}
