-- v1.0 Create tables
CREATE TABLE orders
(
    id            SERIAL PRIMARY KEY,
    uid           UUID         NOT NULL,
    status        VARCHAR(255) NOT NULL
        CHECK ( status IN ('NEW', 'PROCESSED', 'DENIED', 'CANCELED') ),
    created_date  TIMESTAMP    NOT NULL,
    created_user  VARCHAR      NOT NULL,
    modified_date TIMESTAMP    NOT NULL,
    modified_user VARCHAR      NOT NULL
);

CREATE UNIQUE INDEX ux_orders_uid ON orders (uid);

CREATE TABLE order_items
(
    id       SERIAL PRIMARY KEY,
    order_id INT     NOT NULL
        CONSTRAINT fk_order_items_order_id REFERENCES orders (id)
            ON DELETE CASCADE,
    name     VARCHAR NOT NULL
);

CREATE INDEX ux_order_items_order_id ON order_items (order_id);
