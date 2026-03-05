package com.rces.notificationservice;

public record NotificationRequest(
        Long bidId,
        String eventType
) {
}
