# Запуск локального кластера k8s

```shell
# create local cluster
$ kind create cluster --config kind.yml

# configure ingress
$ kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml

# add helm repo for postgres, prometheus+grafana, jaeger
$ helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
$ helm repo add romanow https://romanow.github.io/helm-charts/
$ helm repo add jaegertracing https://jaegertracing.github.io/helm-charts
$ helm repo add jetstack https://charts.jetstack.io
$ helm repo update

# set local address
$ sudo tee -a /etc/hosts >/dev/null <<EOT
127.0.0.1    store.local
127.0.0.1    orders.local
127.0.0.1    warehouse.local
127.0.0.1    warranty.local
127.0.0.1    grafana.local
127.0.0.1    jaeger.local
127.0.0.1    kibana.local
EOT

# install postgres
$ helm install postgres romanow/postgres --values postgres/values.yaml

# install postgres exporter
$ helm install postgres-exporter prometheus-community/prometheus-postgres-exporter \
  --values=postgres-exporter/values.yaml

# install monitoring (Prometheus Stack)
$ kubectl create secret generic grafana-credentials \
  --from-literal=admin-user=admin \
  --from-literal=admin-password=admin

$ helm install prometheus-stack prometheus-community/kube-prometheus-stack \
  --values monitoring/deploy-values.yaml

# install services
$ helm install store romanow/java-service --values=services/common-values.yaml --values=services/store-values.yaml
$ helm install orders romanow/java-service --values=services/common-values.yaml --values=services/gateway-values.yaml
$ helm install warehouse romanow/java-service --values=services/common-values.yaml --values=services/warehouse-values.yaml
$ helm install warranty romanow/java-service --values=services/common-values.yaml --values=services/warranty-values.yaml

$ cd ../tests/postman
$ newman run -e kind-environment.json --folder=store collection.json

```

#### Grafana + Prometheus

```shell
$ helm upgrade monitoring monitoring-chart --set grafana.domain=local
```

Открыть в браузере [http://grafana.local](http://grafana.local).

##### Dashboard

Импортировать Grafana dashboards: `Create` -> `Import` -> `Import via grafana.com`.

* Node Exporter – [12486](https://grafana.com/grafana/dashboards/12486-node-exporter-full/)
* Spring Boot – [10280](https://grafana.com/grafana/dashboards/10280-microservices-spring-boot-2-1/)
* Kubernetes Global – [15757](https://grafana.com/grafana/dashboards/15757-kubernetes-views-global/)
* Kubernetes Nodes – [15759](https://grafana.com/grafana/dashboards/15759-kubernetes-views-nodes/)
* Kubernetes Namespaces – [15758](https://grafana.com/grafana/dashboards/15758-kubernetes-views-namespaces/)
* Kubernetes Pods – [15760](https://grafana.com/grafana/dashboards/15760-kubernetes-views-pods/)

Другие доски доступны на [Grafana Labs](https://grafana.com/grafana/dashboards/).

## Alerting

* Создать бота: в telegram находим `@BotFather`, вызываем `/newbot`:
    * name: _k8s_monitoring_
    * id: _k8s_monitoring_bot_
* Создать канал _K8S Monitoring_, добавить `@k8s_monitoring_bot` как администратора. Отправить хотя бы одно сообщение в
  группу.
* После этого через Telegram API получить chart ID:
   ```http request
   GET https://api.telegram.org/bot<token>/getUpdates
   ```
* Создать Notification Channel: `Grafana` -> `Alerting` -> `Notification Channels` -> `Telegram Bot`.
* Создать новый dashboard:
    * Title: Request Count
    * Panel: `Query: sum(irate(http_server_requests_seconds_count{application="store"}[5m]))`
    * Alerting:
        * Condition: `Evaluate every: 10s for 0, when avg() of query(A, 10s, now) is above 10`
        * Send to: `Telegram bot`, message: `Too many requests`

#### ELK Stack

```shell
$ helm upgrade elasticsearch elasticsearch-chart

$ helm upgrade logging logging-chart --set kibana.domain=local
```

Открыть в браузере [http://kibana.local](http://kibana.local).

#### Jaeger

```shell
# для работы требуется ElasticSearch
$ helm upgrade jaeger jaeger-chart --set domain=local
```

Открыть в браузере [http://jaeger.local](http://jaeger.local).
