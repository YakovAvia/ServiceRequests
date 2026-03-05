package com.rces.requestservice.bids;

import com.rces.requestservice.bids.domain.Bid;
import com.rces.requestservice.bids.domain.BidItem;

import java.time.Instant;
import java.util.List;

public record BidResponse(

        Long id,
        BidStatus status,
        Instant createAt,
        List<BidItemResponse> items

) {

    public static BidResponse from(Bid bid) {

        List<BidItemResponse> items = bid.getItems().stream()
                .map(BidItemResponse::from)
                .toList();

        return new BidResponse(
                bid.getId(),
                bid.getStatus(),
                bid.getCreateAt(),
                items
        );
    }

    public record BidItemResponse(
            Long bidId,
            String itemName,
            int quantity
    ) {
        public static BidItemResponse from(BidItem item) {
            return new BidItemResponse(
                    item.getBid().getId(),
                    item.getItemName(),
                    item.getQuantity()
            );
        }
    }
}
