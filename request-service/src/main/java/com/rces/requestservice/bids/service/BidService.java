package com.rces.requestservice.bids.service;

import com.rces.requestservice.bids.BidResponse;
import com.rces.requestservice.bids.CreateBidRequest;

public interface BidService {

    BidResponse createBid(CreateBidRequest request);

    BidResponse getBidWithItems(Long id);

    BidResponse updateBid(BidUpdate bidUpdate);

    void deleteBid(Long id);

}
