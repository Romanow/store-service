# Order Service

## Описание API

1. `POST /api/v1/orders/{userUid}` – сделать заказ от имени пользователя;
2. `GET /api/v1/orders/{userUid}/{orderUid}` – получить информацию по конкретному заказу пользователя;
3. `GET /api/v1/orders/{userUid}` – получить все заказы пользователя;
4. `POST /api/v1/orders/{orderUid}/warranty` – запрос гарантии по заказу;
5. `DELETE /api/v1/orders/{orderUid}` – вернуть заказ.

## Логика работы

Сервис ответственен за работу с заказом, получение товара со склада (запрос к Warehouse) и создание гарантии (запрос к
Warranty). При запросе достается заказ `order`, из него получаем `item_uid` и с этим параметром выполняются необходимые
запросы к Warehouse и Warranty.

## Структура таблиц

```sql
CREATE TABLE orders
(
    id         SERIAL
        CONSTRAINT orders_pkey PRIMARY KEY,
    item_uid   uuid         NOT NULL,
    order_date TIMESTAMP    NOT NULL,
    order_uid  uuid         NOT NULL
        CONSTRAINT idx_orders_order_uid UNIQUE,
    status     VARCHAR(255) NOT NULL,
    user_id    VARCHAR(80)  NOT NULL
);
```
