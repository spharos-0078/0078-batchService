package com.pieceofcake.batch_service.piece.presentation;

import com.pieceofcake.batch_service.common.entity.BaseResponseEntity;
import com.pieceofcake.batch_service.piece.application.BestPieceProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/best")
@RequiredArgsConstructor
@RestController
public class BestPieceController {

    private final BestPieceProductService bestPieceProductService;

    @Operation(summary = "베스트 조각 상품 조회 API", description = "현재 기준 베스트 조각 상품 목록을 조회합니다.")
    @GetMapping
    public BaseResponseEntity<List<String>> getBestPieceProduct(){
        return new BaseResponseEntity<>(bestPieceProductService.getBestPieceProductUuids());
    }
}
