package com.rces.requestservice.bids.controller;

import com.rces.requestservice.bids.BidResponse;
import com.rces.requestservice.bids.CreateBidRequest;
import com.rces.requestservice.bids.domain.Bid;
import com.rces.requestservice.bids.service.BidService;
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
        Bid bid = bidService.createBid(request);
        BidResponse response = BidResponse.from(bid);

        log.info("Заявка успешно создана, данные созданной заявки: {}", response);
        return ResponseEntity.created(URI.create("/bid/" + bid.getId())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BidResponse> getBidWithItems(@PathVariable Long id) {
        Bid bid = bidService.getBidWithItems(id);
        BidResponse response = BidResponse.from(bid);
        return ResponseEntity.ok(response);
    }

}
