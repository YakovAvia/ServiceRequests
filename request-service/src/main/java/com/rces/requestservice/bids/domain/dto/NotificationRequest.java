package com.rces.requestservice.bids.domain.dto;

public record NotificationRequest(
        Long bidId,
        String eventType
) {
}
