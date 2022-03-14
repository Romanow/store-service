# Store Service

## Описание API

1. `GET /api/v1/store/{userUid}/orders` – получить список заказов пользователя;
1. `GET /api/v1/store/{userUid}/{orderUid}` – информация по конкретному заказу;
1. `POST /api/v1/store/{userUid}/{orderUid}/warranty` – запрос гарантии по заказу;
1. `POST /api/v1/store/{userUid}/purchase` – выполнить покупку;
1. `POST /api/v1/store/{userUid}/{orderUid}/refund` – вернуть заказ;

[https://store-service-app.herokuapp.com/api-docs](https://store-service-app.herokuapp.com/api-docs)

## Логика работы

Сервис является своеобразным gateway, все запросы проходят через него от имени пользователя. Информация о заказах
собирается с Order Service, а потом опционально с Warehouse и Warranty Service.   
Остальные методы проверяют пользователя и делегируют запрос дальше на OrderService, т.к. вся информация о заказе
хранится там.

## Структура таблиц

```postgresql
CREATE TABLE users
(
    id       SERIAL
        CONSTRAINT users_pkey PRIMARY KEY,
    name     VARCHAR(255) NOT NULL
        CONSTRAINT idx_user_name UNIQUE,
    user_uid uuid         NOT NULL
        CONSTRAINT idx_user_user_uid UNIQUE
);
```

## Данные в БД

id | name |user_uid
--- | ---- | ---
1  | Alex |6d2cb5a0-943c-4b96-9aa6-89eac7bdfd2b
