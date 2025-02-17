# Warranty Service

## Описание API

1. `GET /api/v1/warranty/{itemUid}` – информация о статусе гарантии;
2. `POST /api/v1/warranty/{itemUid}/warranty` – запрос решения по гарантии;
3. `POST /api/v1/warranty/{itemUid}` – запрос на начало гарантийного периода;
4. `DELETE /api/v1/warranty/{itemUid}` – запрос на закрытие гарантии.

## Логика работы

Гарантия привязана к `item_uid`, каждая запись о гарантии имеет три статуса: `ON_WARRANTY`, `USE_WARRANTY`,
`REMOVED_FROM_WARRANTY`. При создании заказа (метод 3) создается запись и устанавливается статус `ON_WARRANTY`.
При закрытии заказа (метод 4) устанавливается статус `REMOVED_FROM_WARRANTY`.
При гарантийном запросе (метод 2) проверяется статус гарантии, если статус отличен от `ON_WARRANY`, то решение
`REFUSED`. В запросе от Warehouse приходит количество доступных товаров. Если товар присутствует на складе, то
решение `RETURN`, иначе `FIXING`.

## Структура таблиц

```sql
CREATE TABLE warranty
(
    id            SERIAL
        CONSTRAINT warranty_pkey PRIMARY KEY,
    comment       VARCHAR(1024),
    item_uid      UUID         NOT NULL
        CONSTRAINT idx_warranty_item_uid UNIQUE,
    status        VARCHAR(255) NOT NULL,
    warranty_date TIMESTAMP    NOT NULL
);
```
