package com.rces.requestservice.bids.service.impl;

import com.rces.requestservice.bids.CreateBidRequest;
import com.rces.requestservice.bids.domain.Bid;
import com.rces.requestservice.bids.domain.BidItem;
import com.rces.requestservice.bids.domain.dto.BidCreateEvent;
import com.rces.requestservice.bids.exception.NotFoundOrderException;
import com.rces.requestservice.bids.metrics.annotation.BusinessMetric;
import com.rces.requestservice.bids.repository.BidRepository;
import com.rces.requestservice.bids.service.BidService;
import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.api.trace.Span;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @BusinessMetric(
            value = "bids.create",
            tags = {"operation=create", "type=write"}
    )
    @Observed(name = "bid.creation", contextualName = "create-bid")
    @SneakyThrows
    public Bid createBid(CreateBidRequest request) {

        log.info("В метод createBid получен запрос: {}", request);

        //TODO имитация проблемы удалю
        int random = new Random().nextInt(100);
        log.info("Сгенерировалось случайное число: {}", random);

        if (random < 30) {
            log.error("Возникли проблемы с сохранением заявки");
            throw new RuntimeException("Ну типа ошибка соединения с бд");
        }

        if (random > 70) {
            log.info("Сервис BidService замедлился");
            Thread.sleep(200);
        }

        log.info("Передает items в заявку : {}", request.items());

        List<BidItem> items = request.items().stream()
                .map(item -> new BidItem(
                        item.itemName(),
                        item.quantity()
                ))
                .toList();

        int bidNumber = bidRepository.findAll().size();

        Bid bid = new Bid(bidNumber, items);

        Bid saveBid = bidRepository.saveAndFlush(bid);

        try {
            MDC.put("bid_id", saveBid.getId().toString());
            MDC.put("bid_status", saveBid.getStatus().name());

            log.info("Отправляем инфо о заказе, id: {}", saveBid.getId());

            eventPublisher.publishEvent(
                    BidCreateEvent.of(
                            saveBid.getId(),
                            MDC.getCopyOfContextMap()
                    )
            );

            log.info("Успешно сохранено");

            Span.current().setAttribute("bid.id", saveBid.getId());

            return saveBid;
        } finally {
            MDC.remove("bid_id");
            MDC.remove("bid_status");
        }

    }

    @Transactional(readOnly = true)
    @BusinessMetric(
            value = "bid.retrieved",
            tags = {"operation=get", "type=read"}
    )
    public Bid getBidWithItems(Long id) {

        log.debug("В метод getBidWithItems получен запрос поиска bid по id: {}", id);

        Bid bid = bidRepository.findWithItemsById(id).orElseThrow(
                () -> new NotFoundOrderException("Bid not found")
        );

        log.debug("Результат успешно найден");
        return bid;
    }

}
