# Jaeger Operator

```shell
# устанавливаем cert manager CRD
$ kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.10.0/cert-manager.crds.yaml

# устанавливаем cert manager
$ helm install cert-manager jetstack/cert-manager \
    --namespace cert-manager \
    --create-namespace \
    --version v1.10.0

# устанавливаем Jaeger Operator
$ helm install jaeger-operator jaegertracing/jaeger-operator \
    --values=deploy-values.yml
```
