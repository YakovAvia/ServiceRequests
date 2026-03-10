package com.rces.requestservice.bids.service.impl;

import com.rces.requestservice.bids.BidResponse;
import com.rces.requestservice.bids.CreateBidRequest;
import com.rces.requestservice.bids.domain.Bid;
import com.rces.requestservice.bids.domain.BidItem;
import com.rces.requestservice.bids.domain.dto.BidCreateEvent;
import com.rces.requestservice.bids.exception.NotFoundOrderException;
import com.rces.requestservice.bids.metrics.annotation.BusinessMetric;
import com.rces.requestservice.bids.repository.BidRepository;
import com.rces.requestservice.bids.service.BidService;
import com.rces.requestservice.bids.service.BidUpdate;
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
    public BidResponse createBid(CreateBidRequest request) {

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

            return BidResponse.from(saveBid);
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
    public BidResponse getBidWithItems(Long id) {

        log.info("В метод getBidWithItems получен запрос поиска bid по id: {}", id);

        Bid bid = bidRepository.findWithItemsById(id).orElseThrow(
                () -> new NotFoundOrderException("Bid not found")
        );

        try {
            MDC.put("bid_id", bid.getId().toString());
            MDC.put("bid_status", bid.getStatus().name());

            log.info("Отправляем инфо о заказе, ID: {} ", bid.getId());

            eventPublisher.publishEvent(
                    BidCreateEvent.of(
                            bid.getId(),
                            MDC.getCopyOfContextMap()
                    )
            );

            Span.current().setAttribute("bid.id", bid.getId());

            return BidResponse.from(bid);
        } finally {
            MDC.remove("bid_id");
            MDC.remove("bid_status");
        }
    }

    @Transactional
    @BusinessMetric(
            value = "bid.updated",
            tags = {"operation=patch, type=write"}
    )
    public BidResponse updateBid(BidUpdate bidUpdate) {

        log.info("Метод начинает обработку на обновление заявки с ID: {}", bidUpdate.id());

        Bid bidRequest = bidRepository.findById(bidUpdate.id()).orElseThrow(
                () -> new NotFoundOrderException("Заявка не найдена")
        );

        bidRequest.setStatus(bidUpdate.status());
        for (BidItem item : bidUpdate.items()) {
            bidRequest.getItems().stream()
                    .filter(i -> !i.getItemName().equals(item.getItemName()))
                    .forEach(i -> {
                        i.setItemName(item.getItemName());
                        i.setQuantity(item.getQuantity());
                    });
        }

        try {
            MDC.put("bid_id", bidRequest.getId().toString());
            MDC.put("bid_status", bidRequest.getStatus().name());

            log.info("Отправляем информацию об изменении заявки с ID {}", bidRequest.getId());

            eventPublisher.publishEvent(
                    BidCreateEvent.of(
                            bidRequest.getId(),
                            MDC.getCopyOfContextMap()
                    )
            );

            Span.current().setAttribute("bid.id", bidRequest.getId());

            return BidResponse.from(bidRequest);

        } finally {
            MDC.remove("bid_id");
            MDC.remove("bid_status");
        }
    }

    @Transactional
    @BusinessMetric(
            value = "bid.deleted",
            tags = {"operation=deleted", "type=deleted"}
    )
    public void deleteBid(Long id) {
        Bid bid = bidRepository.findById(id).orElseThrow(
                () -> new NotFoundOrderException("Заявка с ID " + id + ", уже удалена или не существует!"));

        try {

            MDC.put("bid_id", bid.getId().toString());
            MDC.put("bid_status", bid.getStatus().name());

            log.info("Метод начинает обработку на удаление заявки с ID: {}", id);

            eventPublisher.publishEvent(
                    BidCreateEvent.of(
                            bid.getId(),
                            MDC.getCopyOfContextMap()
                    )
            );

            Span.current().setAttribute("bid.id", bid.getId());

            bidRepository.delete(bid);
            log.info("Метод удалил заявку с ID: {}", id);
        } finally {
            MDC.remove("bid_id");
            MDC.remove("bid_status");
        }
    }

}
