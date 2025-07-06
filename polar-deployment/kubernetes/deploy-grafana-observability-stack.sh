#!/bin/sh

set -euo pipefail

echo "\n🔭  Observability stack deployment started.\n"

cd platform/development/services/grafana

#kubectl apply -f resources/namespace.yml

helm repo add grafana https://grafana.github.io/helm-charts
helm repo update

echo "\n📦 Installing Tempo..."

helm upgrade --install tempo grafana/tempo \
  --values helm/tempo-values.yml --version 1.9.0

echo "\n⌛ Waiting for Tempo to be ready..."

while [ $(kubectl get pod -l app.kubernetes.io/name=tempo | wc -l) -eq 0 ] ; do
  sleep 5
done

kubectl wait \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/name=tempo \
  --timeout=90s

echo "\n📦 Installing Grafana, Loki, Prometheus, and Promtail..."

kubectl apply -f resources/dashboards

helm upgrade --install loki-stack grafana/loki-stack \
  --values helm/loki-stack-values.yml --version 2.10.2

sleep 5

echo "\n⌛ Waiting for Promtail to be ready..."

while [ $(kubectl get pod -l app.kubernetes.io/name=promtail | wc -l) -eq 0 ] ; do
  sleep 5
done

kubectl wait \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/name=promtail \
  --timeout=90s

echo "\n⌛ Waiting for Prometheus to be ready..."

while [ $(kubectl get pod -l app=prometheus | wc -l) -eq 0 ] ; do
  sleep 5
done

kubectl wait \
  --for=condition=ready pod \
  --selector=app=prometheus \
  --timeout=90s

echo "\n⌛ Waiting for Loki to be ready..."

while [ $(kubectl get pod -l app=loki | wc -l) -eq 0 ] ; do
  sleep 5
done

kubectl wait \
  --for=condition=ready pod \
  --selector=app=loki \
  --timeout=90s

echo "\n⌛ Waiting for Grafana to be ready..."

while [ $(kubectl get pod -l app.kubernetes.io/name=grafana | wc -l) -eq 0 ] ; do
  sleep 5
done

kubectl wait \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/name=grafana \
  --timeout=90s

echo "\n✅  Grafana observability stack has been successfully deployed."

echo "\n🔐 Your Grafana admin credentials...\n"

echo "Admin Username: user"
echo "Admin Password: $(kubectl get secret  loki-stack-grafana -o jsonpath="{.data.admin-password}" | base64 --decode)"

echo "\n🔭  Observability stack deployment completed.\n"