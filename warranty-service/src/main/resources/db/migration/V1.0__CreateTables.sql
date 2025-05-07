-- v1.0 Create tables
CREATE TABLE warranty
(
    id            SERIAL PRIMARY KEY,
    comment       VARCHAR(1024),
    order_uid     UUID         NOT NULL,
    name          VARCHAR      NOT NULL,
    status        VARCHAR(255) NOT NULL
        CHECK ( status IN ('ON_WARRANTY', 'WARRANTY_USED', 'REMOVED_FROM_WARRANTY') ),
    created_date  TIMESTAMP    NOT NULL,
    modified_date TIMESTAMP    NOT NULL,
    modified_user VARCHAR      NOT NULL
);

CREATE UNIQUE INDEX idx_warranty_order_uid_name ON warranty (order_uid, name);
