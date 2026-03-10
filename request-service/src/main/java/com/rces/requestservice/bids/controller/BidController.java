package com.rces.requestservice.bids.controller;

import com.rces.requestservice.bids.BidResponse;
import com.rces.requestservice.bids.CreateBidRequest;
import com.rces.requestservice.bids.service.BidService;
import com.rces.requestservice.bids.service.BidUpdate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bid")
@Slf4j
public class BidController {

    private final BidService bidService;

    @PostMapping
    public ResponseEntity<BidResponse> createBid(
            @Valid @RequestBody CreateBidRequest request
    ) {
        log.info("Пришел запрос на создание заявки, данные в заявке: {}", request);
        BidResponse response = bidService.createBid(request);

        log.info("Заявка успешно создана, данные созданной заявки: {}", response);
        return ResponseEntity.created(URI.create("/bid/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BidResponse> getBidWithItems(
            @PathVariable Long id
    ) {
        log.info("Пришел запрос на получение заявки с вложенными в нее деталями, ID: {}", id);
        BidResponse response = bidService.getBidWithItems(id);

        log.info("Заявка успешно получена и передана, заявка: {}", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update")
    public ResponseEntity<BidResponse> updateBid(
            @RequestBody BidUpdate bidUpdate
    ) {
        log.info("Пришел запрос на обновление заявки с ID: {}", bidUpdate.id());
        BidResponse response = bidService.updateBid(bidUpdate);
        log.info("Заявка с ID: {}, успешно обновлена!", response.id());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBid(
            @PathVariable Long id
    ) {
        log.info("Пришел запрос на удаление заявки с ID: {}", id);
        bidService.deleteBid(id);
        log.info("Заявка с ID {}, успешно удалена!", id);
        return ResponseEntity.noContent().build();
    }

}
