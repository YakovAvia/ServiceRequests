package com.rces.requestservice.bids.service;

import com.rces.requestservice.bids.BidStatus;
import com.rces.requestservice.bids.domain.BidItem;

import java.util.List;

public record BidUpdate(
        Long id,
        BidStatus status,
        List<BidItem> items
) {
}
