package com.rces.requestservice.bids.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bid_item")
@Getter
@Setter
@NoArgsConstructor
public class BidItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bid_id", nullable = false)
    private Bid bid;

    public BidItem(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }

}
