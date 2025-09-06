#!/bin/bash
# scripts/shell/start-container.sh

set -euo pipefail

NAMESPACE="recipe-management"
DEPLOYMENT="recipe-management-service"

COLUMNS=$(tput cols 2>/dev/null || echo 80)

print_separator() {
  local char="${1:-=}"
  local width="${COLUMNS:-80}"
  printf '%*s\n' "$width" '' | tr ' ' "$char"
}

if ! minikube status | grep -q "Running"; then
  print_separator "="
  echo "🚀 Starting Minikube..."
  print_separator "-"
  minikube start
fi

print_separator "="
echo "🔄 Scaling deployment '$DEPLOYMENT' in namespace '$NAMESPACE' to 1 replica..."
print_separator "-"

kubectl scale deployment "$DEPLOYMENT" --replicas=1 -n "$NAMESPACE"

print_separator "="
echo "⏳ Waiting for pod to be ready..."
print_separator "-"

kubectl wait --namespace="$NAMESPACE" \
  --for=condition=Ready pod \
  --selector=app=recipe-management-service \
  --timeout=90s

print_separator "="
echo "✅ Deployment started."
print_separator "="
