package com.rces.requestservice.bids.service;

import com.rces.requestservice.bids.BidResponse;
import com.rces.requestservice.bids.CreateBidRequest;
import com.rces.requestservice.bids.domain.dto.AddBidItemRequest;
import jakarta.validation.Valid;

import java.util.List;

public interface BidService {

    BidResponse createBid(CreateBidRequest request);

    BidResponse getBidWithItems(Long id);

    BidResponse updateBid(BidUpdate bidUpdate);

    void deleteBid(Long id);

    void deleteBidItemId(Long id);

    BidResponse addBidItem(AddBidItemRequest itemRequests);
}
