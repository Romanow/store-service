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
127.0.0.1    gateway.local
127.0.0.1    grafana.local
127.0.0.1    jaeger.local
127.0.0.1    kibana.local
EOT

# install postgres
$ helm install postgres romanow/postgres --values postgres/values.yaml

# install services
$ helm install gateway romanow/java-service --values=services/common-values.yaml --values=services/gateway-values.yaml
$ helm install store romanow/java-service --values=services/common-values.yaml --values=services/store-values.yaml
$ helm install warehouse romanow/java-service --values=services/common-values.yaml --values=services/warehouse-values.yaml
$ helm install warranty romanow/java-service --values=services/common-values.yaml --values=services/warranty-values.yaml

$ cd ../tests/postman
$ newman run -e k8s-auth0.json collection.json

```

## Postgres Exporter

```shell
$ helm install postgres-exporter prometheus-community/prometheus-postgres-exporter --values=postgres-exporter/values.yaml
```

#### Grafana + Prometheus

```shell

$ helm install node-exporter romanow/node-exporter
$ helm install kube-state-metrics romanow/kube-state-metrics
$ helm install prometheus romanow/prometheus
$ helm install grafana romanow/grafana --set ingress.domain=local
```

Install Prometheus Operator:

```shell
$ kubectl create secret generic grafana-credentials \
  --from-literal=admin-user=admin \
  --from-literal=admin-password=admin

$ helm install prometheus-stack prometheus-community/kube-prometheus-stack --values monitoring/deploy-values.yaml
```

Открыть в браузере [http://grafana.local](http://grafana.local).

#### ELK Stack

```shell
$ helm install fluent-bit romanow/fluent-bit
$ helm install elasticsearch romanow/elastic --values=elastic/values.yaml
$ helm install kibana romanow/kibana --set ingress.domain=local
```

Открыть в браузере [http://kibana.local](http://kibana.local).

#### Jaeger

```shell
$ helm install elasticsearch romanow/elastic --values=elastic/values.yaml
$ helm install prometheus romanow/prometheus
$ helm install jaeger romanow/jaeger --set ingress.domain=local
```

Открыть в браузере [http://jaeger.local](http://jaeger.local).
