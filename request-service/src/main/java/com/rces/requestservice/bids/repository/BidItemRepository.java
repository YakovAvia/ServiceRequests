package com.rces.requestservice.bids.repository;

import com.rces.requestservice.bids.domain.BidItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidItemRepository extends JpaRepository<BidItem, Long> {

}
