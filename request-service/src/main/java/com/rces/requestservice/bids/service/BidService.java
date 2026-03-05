package com.rces.requestservice.bids.service;

import com.rces.requestservice.bids.CreateBidRequest;
import com.rces.requestservice.bids.domain.Bid;

public interface BidService {

    Bid createBid(CreateBidRequest request);

    Bid getBidWithItems(Long id);

}
