package com.rces.requestservice.bids.service.impl;

import com.rces.requestservice.bids.BidResponse;
import com.rces.requestservice.bids.BidStatus;
import com.rces.requestservice.bids.CreateBidRequest;
import com.rces.requestservice.bids.domain.Bid;
import com.rces.requestservice.bids.domain.BidItem;
import com.rces.requestservice.bids.domain.dto.AddBidItemRequest;
import com.rces.requestservice.bids.domain.dto.BidCreateEvent;
import com.rces.requestservice.bids.exception.NotFoundBidItemException;
import com.rces.requestservice.bids.exception.NotFoundOrderException;
import com.rces.requestservice.bids.exception.ValidateBidException;
import com.rces.requestservice.bids.metrics.annotation.BusinessMetric;
import com.rces.requestservice.bids.repository.BidItemRepository;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final BidItemRepository bidItemRepository;

    @Transactional
    @BusinessMetric(
            value = "bids.create",
            tags = {"operation=create", "type=write"}
    )
    @Observed(name = "bid.creation", contextualName = "create-bid")
    @SneakyThrows
    @Override
    public BidResponse createBid(CreateBidRequest request) {

        log.info("В метод createBid получен запрос: {}", request);

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
    @Override
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
    @Override
    public BidResponse updateBid(BidUpdate bidUpdate) {

        log.info("Метод начинает обработку на обновление заявки с ID: {}", bidUpdate.id());

        Bid bidRequest = bidRepository.findById(bidUpdate.id()).orElseThrow(
                () -> new NotFoundOrderException("Заявка не найдена")
        );

        bidRequest.setStatus(bidUpdate.status());
        if (bidRequest.getStatus().equals(BidStatus.NEW)) {

        } else {
            throw new ValidateBidException("Изменять информацию в заявке можно только в статусе " + BidStatus.NEW.getEnumName());
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
    @Override
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

    @Transactional
    @Override
    public void deleteBidItemId(Long id) {

        BidItem item = bidItemRepository.findById(id).orElseThrow(
                () -> new NotFoundBidItemException("Предмет с ID " + id + " не найден!"));
        Bid bid = bidRepository.findById(item.getBid().getId()).orElseThrow(
                () -> new NotFoundOrderException("Заявка с ID " + item.getBid().getId() + " не найдена!"));
        try {

            MDC.put("item_id", String.valueOf(item.getId()));
            MDC.put("bid_status", String.valueOf(bid.getStatus()));

            log.info("Удаляем BidItem с ID {}", id);

            if (item.getBid() != null && bid.getStatus().equals(BidStatus.NEW)) {
                bidItemRepository.delete(item);
            }

            log.info("BidItem с ID {}, успешно удален", id);
        } finally {
            MDC.remove("item_id");
            MDC.remove("bid_status");
        }
    }

    @Transactional
    @Override
    public BidResponse addBidItem(AddBidItemRequest bidItemRequest) {

        List<BidItem> bidItems = new ArrayList<>();

        log.info("Добавляем BidItem в Bid");

        Bid bid = bidRepository.findById(bidItemRequest.id()).orElseThrow(
                () -> new NotFoundOrderException("Заявка с ID " + bidItemRequest.id() +" не найдена"));

        if (bidItemRequest.item() != null && !bidItemRequest.item().isEmpty()) {
            bidItems = bidItemRequest.item().stream()
                    .filter()
                    .map(bidItem -> new BidItem(
                            bidItem.itemName(),
                            bidItem.quantity()
                    ))
                    .toList();
        }

        bid.getItems().addAll(bidItems);

        return BidResponse.from(bid);
    }
}
