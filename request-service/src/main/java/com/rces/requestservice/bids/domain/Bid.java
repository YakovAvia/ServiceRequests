package com.rces.requestservice.bids.domain;

import com.rces.requestservice.bids.BidStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bid")
@Getter
@Setter
@NoArgsConstructor
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BidStatus status;

    @Column(name = "bid_number", nullable = false, unique = true)
    private int bidNumber;

    @Column(name = "create_at")
    private Instant createAt;

    @OneToMany(mappedBy = "bid", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BidItem> items = new ArrayList<>();

    public Bid(int bidNumber, List<BidItem> items) {
        this.status = BidStatus.NEW;
        this.createAt = Instant.now();
        this.bidNumber = bidNumber;
        this.items.addAll(items);

        items.forEach(i -> i.setBid(this));
    }

}
