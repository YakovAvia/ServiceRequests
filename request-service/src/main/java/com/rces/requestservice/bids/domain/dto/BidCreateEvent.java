package com.rces.requestservice.bids.domain.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record BidCreateEvent(
        Long bidId,
        Map<String, String> mdcContext,
        LocalDateTime timestamp
) {

    public static BidCreateEvent of(Long bidId, Map<String, String> mdcContext) {
        return new BidCreateEvent(
                bidId,
                mdcContext,
                LocalDateTime.now()
        );
    }

}
