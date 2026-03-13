package com.rces.requestservice.bids.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "bid_item")
@Getter
@Setter
@NoArgsConstructor
public class BidItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "timer")
    private LocalTime timer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bid_id", nullable = false)
    private Bid bid;

    public BidItem(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public BidItem(String itemName, int quantity, Bid bid) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.bid = bid;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        BidItem item = (BidItem) obj;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
