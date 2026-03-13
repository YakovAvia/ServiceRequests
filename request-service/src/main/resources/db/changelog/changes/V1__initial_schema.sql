CREATE TABLE bid
(
    id         BIGSERIAL PRIMARY KEY,
    status     VARCHAR(50)              NOT NULL,
    bid_number INTEGER                  NOT NULL,
    creat_at   TIMESTAMP WITH TIME ZONE NOT NULL

        CONSTRAINT check_status CHECK ( status IN ('NEW', 'WORK', 'REJECTED', 'COMPLETED', 'STOPPED'))

);

CREATE TABLE bid_item
(
    id        BIGSERIAL PRIMARY KEY,
    bid_id    BIGINT       not null,
    item_name VARCHAR(255) not null,
    quantity  INTEGER      NOT NULL CHECK ( quantity > 0 ),

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (bid_id)
            REFERENCES bid (id)
            ON DELETE CASCADE

);

CREATE INDEX idx_bid_items_id ON bid_item (bid_id);
CREATE INDEX idx_bid_status ON bid (status);