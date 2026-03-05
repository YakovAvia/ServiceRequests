package com.rces.requestservice.bids;

import com.rces.requestservice.bids.client.NotificationClient;
import com.rces.requestservice.bids.domain.dto.BidCreateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationClient notificationClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBidCreated(BidCreateEvent event) {

        log.debug("Получено событие о создании заявки: {}, дата: {}", event.bidId(), event.timestamp());

        try {
            notificationClient.notifyBidCreate(event);
            log.debug("Уведомление отправлено для заявки: {}", event.bidId());
        }catch (Exception e) {
            log.error("Ошибка при отправке уведомления для заявки: {}", event.bidId(), e);
        }
    }
}
