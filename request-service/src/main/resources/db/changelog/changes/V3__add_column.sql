ALTER TABLE bid
    ADD COLUMN description VARCHAR(512);

ALTER TABLE bid_item
    ADD COLUMN timer TIME;