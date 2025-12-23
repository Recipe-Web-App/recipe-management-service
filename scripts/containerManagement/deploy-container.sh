#!/bin/bash
# scripts/shell/deploy-container.sh

set -euo pipefail

NAMESPACE="recipe-management"
CONFIG_DIR="k8s"
SECRET_NAME="recipe-management-secrets"
IMAGE_NAME="recipe-management-service"
IMAGE_TAG="latest"
FULL_IMAGE_NAME="${IMAGE_NAME}:${IMAGE_TAG}"

COLUMNS=$(tput cols 2>/dev/null || echo 80)

print_separator() {
  local char="${1:-=}"
  local width="${COLUMNS:-80}"
  printf '%*s\n' "$width" '' | tr ' ' "$char"
}

print_separator "="
echo "🔧 Setting up Minikube environment..."
print_separator "-"
env_status=true
if ! command -v minikube >/dev/null 2>&1; then
  echo "❌ Minikube is not installed. Please install it first."
  env_status=false
else
  echo "✅ Minikube is installed."
fi

if ! command -v kubectl >/dev/null 2>&1; then
  echo "❌ kubectl is not installed. Please install it first."
  env_status=false
else
  echo "✅ kubectl is installed."
fi
if ! command -v docker >/dev/null 2>&1; then
  echo "❌ Docker is not installed. Please install it first."
  env_status=false
else
  echo "✅ Docker is installed."
fi
if ! command -v jq >/dev/null 2>&1; then
  echo "❌ jq is not installed. Please install it first."
  env_status=false
else
  echo "✅ jq is installed."
fi
if ! $env_status; then
  echo "Please resolve the above issues before proceeding."
  exit 1
fi

if ! minikube status >/dev/null 2>&1; then
  print_separator "-"
  echo "🚀 Starting Minikube..."
  minikube start

  if ! minikube addons list | grep -q 'ingress *enabled'; then
    echo "🔌 Enabling Minikube ingress addon..."
    minikube addons enable ingress
    echo "✅ Minikube started."
  fi
else
  echo "✅ Minikube is already running."
fi

print_separator "="
echo "📂 Ensuring namespace '${NAMESPACE}' exists..."
print_separator "-"

if kubectl get namespace "$NAMESPACE" >/dev/null 2>&1; then
    echo "✅ '$NAMESPACE' namespace already exists."
else
    kubectl create namespace "$NAMESPACE"
    echo "✅ '$NAMESPACE' namespace created."
fi

print_separator "="
echo "🔧 Loading environment variables from .env file (if present)..."
print_separator "-"

if [ -f .env ]; then
    set -o allexport
    BEFORE_ENV=$(mktemp)
    AFTER_ENV=$(mktemp)
    env | cut -d= -f1 | sort > "$BEFORE_ENV"
    # shellcheck source=.env disable=SC1091
    source .env
    env | cut -d= -f1 | sort > "$AFTER_ENV"
    echo "✅ Loaded variables from .env:"
    comm -13 "$BEFORE_ENV" "$AFTER_ENV"
    rm -f "$BEFORE_ENV" "$AFTER_ENV"
    set +o allexport
fi

print_separator "="
echo "🐳 Building Docker image: ${FULL_IMAGE_NAME} (inside Minikube Docker daemon)"
print_separator '-'

eval "$(minikube docker-env)"
docker build -t "$FULL_IMAGE_NAME" .
echo "✅ Docker image '${FULL_IMAGE_NAME}' built successfully."

print_separator "="
echo "⚙️  Creating/Updating ConfigMap from env..."
print_separator "-"

envsubst < "${CONFIG_DIR}/configmap-template.yaml" | kubectl apply -f -

print_separator "="
echo "🔐 Creating/updating Secret..."
print_separator "-"

kubectl delete secret "$SECRET_NAME" -n "$NAMESPACE" --ignore-not-found
envsubst < "${CONFIG_DIR}/secret-template.yaml" | kubectl apply -f -

print_separator "="
echo "📦 Deploying Recipe Management Service container..."
print_separator "-"

kubectl apply -f "${CONFIG_DIR}/deployment.yaml"

print_separator "="
echo "🌐 Exposing Recipe Management Service via ClusterIP Service..."
print_separator "-"

kubectl apply -f "${CONFIG_DIR}/service.yaml"

print_separator "="
echo "⏳ Waiting for Ingress controller to be ready..."
print_separator "-"

kubectl wait --namespace ingress-nginx \
    --for=condition=Ready pod \
    --selector=app.kubernetes.io/component=controller \
    --timeout=90s

print_separator "-"
echo "✅ Ingress controller is running."

print_separator "="
echo "📥 Applying Ingress resource..."
print_separator "-"

kubectl apply -f "${CONFIG_DIR}/ingress.yaml"

print_separator "="
echo "⏳ Waiting for Recipe Management Service pod to be ready..."
print_separator "-"

kubectl wait --namespace="$NAMESPACE" \
  --for=condition=Ready pod \
  --selector=app=recipe-management-service \
  --timeout=120s

print_separator "-"
echo "✅ Recipe Management Service is up and running in namespace '$NAMESPACE'.'"

print_separator "="
echo "🔗 Setting up /etc/hosts for recipe-management.local..."
print_separator "-"

MINIKUBE_IP=$(minikube ip)
if grep -q "recipe-management.local" /etc/hosts; then
  echo "🔄 Updating /etc/hosts for recipe-management.local..."
  sed -i "/recipe-management.local/d" /etc/hosts
else
  echo "➕ Adding recipe-management.local to /etc/hosts..."
fi
echo "$MINIKUBE_IP recipe-management.local" >> /etc/hosts
echo "✅ /etc/hosts updated with recipe-management.local pointing to $MINIKUBE_IP"

print_separator "="
echo "🌍 You can now access your app at: http://recipe-management.local/actuator/health"

POD_NAME=$(kubectl get pods -n "$NAMESPACE" -l app=recipe-management-service -o jsonpath="{.items[0].metadata.name}")
SERVICE_JSON=$(kubectl get svc recipe-management-service -n "$NAMESPACE" -o json)
SERVICE_IP=$(echo "$SERVICE_JSON" | jq -r '.spec.clusterIP')
SERVICE_PORT=$(echo "$SERVICE_JSON" | jq -r '.spec.ports[0].port')
INGRESS_HOSTS=$(kubectl get ingress -n "$NAMESPACE" -o jsonpath='{.items[*].spec.rules[*].host}' | tr ' ' '\n' | sort -u | paste -sd ',' -)

print_separator "="
echo "🛰️  Access info:"
echo "  Pod: $POD_NAME"
echo "  Service: $SERVICE_IP:$SERVICE_PORT"
echo "  Ingress Hosts: $INGRESS_HOSTS"
echo "  Health Check: http://recipe-management.local/actuator/health"
print_separator "="
