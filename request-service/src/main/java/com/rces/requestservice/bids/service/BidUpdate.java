package com.rces.requestservice.bids.service;

import com.rces.requestservice.bids.BidStatus;
import com.rces.requestservice.bids.domain.BidItem;

import java.time.LocalTime;
import java.util.List;

public record BidUpdate(
        Long id,
        BidStatus status,
        LocalTime time,
        List<BidItem> items
) {
}
