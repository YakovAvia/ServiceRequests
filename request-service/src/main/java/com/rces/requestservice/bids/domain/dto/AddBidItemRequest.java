package com.rces.requestservice.bids.domain.dto;

import com.rces.requestservice.bids.CreateBidRequest;

import java.util.List;

public record AddBidItemRequest(
        Long bidId,
        List<CreateBidRequest.BidItemRequest> item
) {
}
