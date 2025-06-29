package com.pieceofcake.batch_service.piece.application;

import com.pieceofcake.batch_service.piece.entity.BestPieceProductAggregation;
import com.pieceofcake.batch_service.piece.infrastructure.BestPieceProductAggregationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BestPieceProductServiceImpl implements BestPieceProductService {

    private final BestPieceProductAggregationRepository repository;

    @Override
    public List<String> getBestPieceProductUuids(){
        return repository.findByDateOrderByRankingAsc(LocalDate.now())
                .stream().map(BestPieceProductAggregation::getPieceProductUuid).toList();
    }
}
