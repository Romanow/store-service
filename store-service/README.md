# Store Service

## Описание API

1. `GET /api/v1/store/orders` – получить список заказов пользователя;
2. `GET /api/v1/store/{orderUid}` – информация по конкретному заказу;
3. `POST /api/v1/store/{orderUid}/warranty` – запрос гарантии по заказу;
4. `POST /api/v1/store/purchase` – выполнить покупку;
5. `POST /api/v1/store/{orderUid}/refund` – вернуть заказ;

## Логика работы

Сервис является своеобразным gateway, все запросы проходят через него от имени пользователя. Информация о заказах
собирается с Order Service, а потом опционально с Warehouse и Warranty Service. Остальные методы проверяют пользователя
и делегируют запрос дальше на Order Service, т.к. вся информация о заказе хранится там.
