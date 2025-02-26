-- V 1.0 Create orders table
CREATE TABLE orders
(
    id         SERIAL PRIMARY KEY,
    user_id    VARCHAR(80)  NOT NULL,
    order_uid  uuid         NOT NULL,
    item_uid   uuid         NOT NULL,
    order_date TIMESTAMP    NOT NULL,
    status     VARCHAR(255) NOT NULL
);

CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE UNIQUE INDEX idx_orders_order_uid ON orders (order_uid);
CREATE INDEX idx_orders_user_id_and_order_uid ON orders (user_id, order_uid);
