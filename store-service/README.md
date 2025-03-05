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

Запрос на Warranty Service для получения информации по гарантии:

```http request
GET /warranty/api/private/v1/warranty/{{ orderUid }}
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
POST /store/api/public/v1/orders/purchase
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
POST /warehouse/api/private/v1/items/{{ orderUid }}/take
```

```json
[
    {
        "name": "Lego Technic 8880",
        "count": 1
    }
]
```

На Warehouse проверяется наличие товара: если все товары есть, то уменьшается количество на `count` и делается запрос на
Warranty Service на постановку товаров на гарантию:

```http request
POST /warranty/api/private/v1/warranty/{{ orderUid }}/start
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

```http request
POST /store/api/public/v1/orders/{{ orderUid }}/warranty
```

Запрос (на вход может передаваться не весь набор товаров в заказе):

```json
[
    {
        "name": "Lego Technic 8880",
        "comment": ""
    }
]
```

Ответ:

```json
[
    {
        "name": "Lego Technic 8880",
        "status": "TAKE_NEW"
    }
]
```

Выполняется запрос на Warranty Service:

```http request
POST /warranty/api/public/v1/warranty/{{ orderUid }}/request
```

```json
[
    {
        "name": "Lego Technic 8880",
        "comment": ""
    }
]
```

Ответ:

```json
[
    {
        "name": "Lego Technic 8880",
        "status": "TAKE_NEW"
    }
]
```

На Warranty Service проверяем что товары на гарантии. Если товар не на гарантии, то возвращаем 422 Unprocessable Entity.
Иначе делаем запрос на Warehouse Service для проверки, есть ли еще такие товары в наличии. Если есть, то ставим статус
`TAKE_NEW` иначе `REPAIR`.

Запрос:

```http request
PUT /warehouse/api/private/v1/items
```

```json
[
    "Lego Technic 88880"
]
```

Ответ:

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

### Отменить заказ

```http request
DELETE /store/api/public/v1/orders/{{ orderUid }}/cancel
```

Ответ: 204 No Content.

Выполняется запрос на Warehouse Service для возврата товара на склад:

```http request
DELETE /warehouse/api/private/v1/items/{{ orderUid }}/return
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

Ответ: 204 No Content.

Так же выполняется запрос на Warranty Service для отмены гарантии по заказу:

```http request
DELETE /warranty/api/private/v1/warranty/{{ orderUid }}/stop
```

Ответ: 204 No Content.

После этого заказ помечается статусом `CANCELED`.

## Структура таблиц

[Table Structure](src/main/resources/db/migration/V1.0__CreateTables.sql)
