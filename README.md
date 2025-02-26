[![CI](https://github.com/Romanow/store-service/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Romanow/store-service/actions/workflows/build.yml)
[![pre-commit](https://img.shields.io/badge/pre--commit-enabled-brightgreen?logo=pre-commit)](https://github.com/pre-commit/pre-commit)
[![Release](https://img.shields.io/github/v/release/Romanow/store-service?logo=github&sort=semver)](https://github.com/Romanow/store-service/releases/latest)
[![Store Service](https://img.shields.io/docker/pulls/romanowalex/store-service?logo=docker)](https://hub.docker.com/r/romanowalex/store-service)
[![License](https://img.shields.io/github/license/Romanow/store-service)](https://github.com/Romanow/store-service/blob/master/LICENSE)

# Store Service

GitHub: [romanow/store-service](https://github.com/Romanow/store-service).

## Состав

* [Gateway](gateway/README.md)
* [Store Service](store-service/README.md)
* [Warehouse Service](warehouse-service/README.md)
* [Warranty Service](warranty-service/README.md)

## Взаимодействие сервисов

[//]: # (TODO mermaid)

## Сборка и запуск

Используем [docker-compose.yml](docker-compose.yml):

```shell
# build services
$ ./gradlew clean build

# build docker images
$ docker compose build

# run images
$ docker compose \
    -f docker-compose.yml \
    -f docker-compose.tracing.yml \
    -f docker-compose.logging.yml \
    -f docker-compose.monitoring.yml \
    up -d --wait
```
