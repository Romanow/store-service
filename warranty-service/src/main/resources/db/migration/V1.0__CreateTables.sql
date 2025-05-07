-- v1.0 Create tables
CREATE TABLE warranty
(
    id            SERIAL PRIMARY KEY,
    order_uid     UUID         NOT NULL,
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_date  TIMESTAMP    NOT NULL,
    modified_date TIMESTAMP    NOT NULL,
    modified_user VARCHAR(255) NOT NULL
);

CREATE UNIQUE INDEX idx_warranty_order_uid ON warranty (order_uid);

CREATE TABLE warranty_item
(
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    count         INTEGER      NOT NULL,
    comment       VARCHAR(1024),
    status        VARCHAR(255) NOT NULL
        CHECK ( status IN ('ON_WARRANTY', 'WARRANTY_USED', 'REMOVED_FROM_WARRANTY') ),
    warranty_id   INTEGER      NOT NULL
        CONSTRAINT fk_warranty_item_warranty_id REFERENCES warranty (id)
            ON DELETE CASCADE,
    created_date  TIMESTAMP    NOT NULL,
    modified_date TIMESTAMP    NOT NULL,
    modified_user VARCHAR(255) NOT NULL
);

CREATE UNIQUE INDEX idx_warranty_item_warranty_id ON warranty_item (warranty_id);
CREATE UNIQUE INDEX idx_warranty_item_name ON warranty_item (name);
