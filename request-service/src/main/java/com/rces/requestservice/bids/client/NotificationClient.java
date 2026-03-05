package com.rces.requestservice.bids.client;

import com.rces.requestservice.bids.domain.dto.BidCreateEvent;
import com.rces.requestservice.bids.domain.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationClient {

    private final WebClient webClient;

    @Async
    public void notifyBidCreate(BidCreateEvent event) {

        Map<String, String> mdcContext = event.mdcContext();

        if (mdcContext != null) {
            MDC.setContextMap(mdcContext);
        }

        Long bidId = event.bidId();

        try {

            log.info("Отправка уведомления для заказа {} с traceId: {}",
                    bidId,
                    MDC.get("traceId")
            );

            NotificationRequest request = new NotificationRequest(bidId, "CREATED");

            webClient.post()
                    .uri("/api/notifications")
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } finally {
            MDC.clear();
        }
    }
}
