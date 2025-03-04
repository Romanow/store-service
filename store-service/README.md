# Магазин

## Логика работы

### Посмотреть заказы пользователя

Запрос на Store Service, поднимается `Order`, `OrderItems`, в ответ отдается только локальная информация.

```http request
GET /store/api/public/v1/orders
```

Ответ:

```json
[
    {
        "orderUid": "<uid>",
        "status": "PROCESSED",
        "userId": "romanow",
        "orderDate": "2025-03-04",
        "items": [
            {
                "name": "Lego Technic 8880",
                "count": 1
            }
        ]
    }
]
```

### Посмотреть информацию о конкретном заказе

Запрос на Store Service, поднимается `Order`, `OrderItems`, _параллельно_ делается запрос на Warehouse Service и
Warranty Service.

```http request
GET /store/api/public/v1/orders/{{ orderUid }}
```

Ответ:

```json
[
    {
        "orderUid": "<uid>",
        "status": "PROCESSED",
        "userId": "romanow",
        "orderDate": "2025-03-04",
        "items": [
            {
                "name": "Lego Technic 8880",
                "count": 1,
                "description": "Lego Supercar",
                "manufacturer": "Lego",
                "imageUrl": "<url>",
                "warranty": {
                    "status": "ON_WARRANTY",
                    "comment": "",
                    "startDate": "2025-03-04",
                    "modifiedDate": "2025-03-04"
                }
            }
        ]
    }
]
```

Запрос на Warehouse Service для получения информации по товарам:

```http request
PUT /warehouse/api/private/v1/items
```

Запрос:

```json
[
    "Lego Technic 88880"
]
```

Ответ:

```json
{
    "Lego Technic 8880": {
        "description": "Lego Supercar",
        "manufacturer": "Lego",
        "imageUrl": "<url>"
    }
}
```

Запрос на Warranty Service для получения информации по гарантии:

```http request
GET /warranty/api/private/v1/{{ orderUid }}
```

Ответ:

```json
{
    "Lego Technic 8880": {
        "status": "ON_WARRANTY",
        "comment": "",
        "startDate": "2025-03-04",
        "modifiedDate": "2025-03-04"
    }
}
```

### Сделать заказ

На UI запрашивается список доступных товаров на Warehouse Service: `GET /warehouse/api/public/v1/items`:

```json
[
    {
        "name": "Lego Technic 8880",
        "availableCount": 10,
        "description": "Lego Supercar",
        "manufacturer": "Lego",
        "imageUrl": "<url>"
    }
]
```

Из этого списка составляется корзина и выполняется запрос на Store Service:

```http request
POST /store/api/public/v1/orders
```

Запрос:

```json
[
    {
        "name": "Lego Technic 8880",
        "count": 1
    }
]
```

Ответ: 201 Created + `Location /store/api/public/v1/orders/{{ orderUid }}`.

На Store Service создается заказ в статусе `NEW` (а товары записываются в таблицу `OrderItems`), потом делается запрос
на Warehouse Service на бронирование товаров:

```http request
POST /warehouse/api/private/v1/take
```

```json
{
    "orderUid": "<uid>",
    "items": [
        {
            "name": "Lego Technic 8880",
            "count": 1
        }
    ]
}
```

На Warehouse проверяется наличие товара: если все товары есть, то уменьшается количество на `count` и делается запрос на
Warranty Service на постановку товаров на гарантию:

```http request
POST /warranty/api/private/v1/{{ orderUid }}/start
```

Запрос:

```json
[
    "Lego Technic 8880"
]
```

Ответ: 204 No Content.

На Warranty Service создается запись для каждого товара со статусом `ON_WARRANTY`.

После этого ответ возвращается на Store Service и заказ переводится в статус `PROCESSED`.

Если товаров нет в наличии, то на Store Service возвращается ответ 409 Conflict с пояснением, какого товара нет в
наличии. Заказ переходит в статус `DENIED`.

### Запрос гарантии по товару из заказа



### Отменить заказ

```http request
DELETE /store/api/public/v1/orders/{{ orderUid }}
```

## Структура таблиц

[Table Structure](src/main/resources/db/migration/V1.0__CreateTables.sql)
