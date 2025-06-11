package com.pieceofcake.batch_service.piece.infrastructure;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "piece-service", url = "http://localhost:8000/piece-service/api/v1")
public interface PieceClient {
    @GetMapping
    public List<String> getPieceProductList();
}
