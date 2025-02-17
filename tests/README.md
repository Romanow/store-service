# Тестирование

## Настройка Auth0

1. Регистрируемся на [Auth0](https://auth0.com).
2. Создаем приложение: `Applications` -> `Create Application`: `Native`, заходим в созданное приложение и
   копируем `Client ID` и `Client Secret`.
3. Переходим в `Advanced Settings` -> `Grant Types`: только `Password` (Resource Owner Password Flow).
4. Переходим в `API` -> `Create API`:
    * Name: `Cinema Aggregator Service`;
    * Identifier: `http://store.romanow-alex.ru`;
    * Signing Algorithm: RS256.
5. Настраиваем хранилище паролей: `Settings` -> `Tenant Settings` -> `API Authorization Settings`:
    * Default Audience: `http://store.romanow-alex.ru`;
    * Default Directory: `Username-Password-Authentication`.
6. Создаем тестового пользователя: `User Management` -> `Users` -> `Create User`:
    * Email: `ronin@romanow-alex.ru`;
    * Password: `Qwerty123`;
    * Connection: `Username-Password-Authentication`.

После настройки у вас должен успешно выполняться запрос на проверку получение токена (подставить свои настройки):

```shell
$ curl --location --request POST 'https://romanowalex.eu.auth0.com/oauth/token' \
    --header 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode 'grant_type=password' \
    --data-urlencode 'username=ronin@romanow-alex.ru' \
    --data-urlencode 'password=Qwerty123' \
    --data-urlencode 'scope=openid' \
    --data-urlencode 'client_id=<Client ID>' \
    --data-urlencode 'client_secret=<Client Secret>'
```

В ответ получаем токен:

```json
{
    "access_token": "...",
    "id_token": "...",
    "scope": "openid profile email ...",
    "expires_in": 86400,
    "token_type": "Bearer"
}
```

## Нагрузочное тестирование

```shell
$ brew install k6

$ docker compose \
    -f docker-compose.yml \
    -f docker-compose.tracing.yml \
    -f docker-compose.logging.yml \
    -f docker-compose.monitoring.yml \
    up -d --wait

$ K6_WEB_DASHBOARD=true K6_WEB_DASHBOARD_EXPORT=report.html \
  k6 run \
    -e USERNAME=ronin@romanow-alex.ru \
    -e PASSWORD=Qwerty123 \
    -e CLIENT_ID=<Client ID> \
    -e CLIENT_SECRET=<Client Secret> \
    k6.auth.js
```
