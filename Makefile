# Makefile for recipe-management-service
# Local development commands

.PHONY: help run-local run-local-jar run-local-debug build test clean format check \
	test-unit test-component test-integration test-performance test-all test-single \
	coverage coverage-check coverage-open \
	format-check pom-sort check-all \
	compile install verify \
	docker-build k8s-deploy k8s-start k8s-stop k8s-status k8s-update k8s-cleanup k8s-logs \
	deps deps-updates delombok check-test-files \
	pre-commit ci

# Default target
.DEFAULT_GOAL := help

# Include .env.local for local development targets
ifneq (,$(wildcard .env.local))
    include .env.local
    export
endif

# Colors for output
BLUE := \033[0;34m
GREEN := \033[0;32m
YELLOW := \033[0;33m
NC := \033[0m # No Color

help: ## Show this help message
	@echo "$(BLUE)Recipe Management Service - Development Commands$(NC)"
	@echo ""
	@echo "$(GREEN)Local Development:$(NC)"
	@grep -E '^(run-local|run-local-jar|run-local-debug):.*?## .*$$' Makefile | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(YELLOW)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""
	@echo "$(GREEN)Build:$(NC)"
	@grep -E '^(build|compile|install|verify|clean):.*?## .*$$' Makefile | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(YELLOW)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""
	@echo "$(GREEN)Testing:$(NC)"
	@grep -E '^test[a-zA-Z_-]*:.*?## .*$$' Makefile | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(YELLOW)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""
	@echo "$(GREEN)Coverage:$(NC)"
	@grep -E '^coverage[a-zA-Z_-]*:.*?## .*$$' Makefile | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(YELLOW)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""
	@echo "$(GREEN)Code Quality:$(NC)"
	@grep -E '^(format|format-check|check|check-all|pom-sort):.*?## .*$$' Makefile | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(YELLOW)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""
	@echo "$(GREEN)Docker/Kubernetes:$(NC)"
	@grep -E '^(docker|k8s)-[a-zA-Z_-]+:.*?## .*$$' Makefile | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(YELLOW)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""
	@echo "$(GREEN)Utilities:$(NC)"
	@grep -E '^(deps|deps-updates|delombok|check-test-files):.*?## .*$$' Makefile | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(YELLOW)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""
	@echo "$(GREEN)Workflows:$(NC)"
	@grep -E '^(pre-commit|ci):.*?## .*$$' Makefile | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(YELLOW)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""
	@echo "$(GREEN)Environment:$(NC)"
	@echo "  Uses .env.local for configuration"
	@echo "  Database: $(POSTGRES_HOST):$(POSTGRES_PORT)/$(POSTGRES_DB)"
	@echo "  Profile: $(SPRING_PROFILES_ACTIVE)"

run-local: ## Run locally with Maven (hot reload enabled)
	@echo "$(GREEN)Starting Recipe Management Service...$(NC)"
	@echo "  Database: $(POSTGRES_HOST):$(POSTGRES_PORT)/$(POSTGRES_DB)"
	@echo "  Profile: $(SPRING_PROFILES_ACTIVE)"
	@echo "  OAuth2: $(OAUTH2_SERVICE_ENABLED)"
	@echo ""
	mvn spring-boot:run

run-local-jar: build ## Build and run JAR locally
	@echo "$(GREEN)Starting Recipe Management Service (JAR)...$(NC)"
	@echo "  Database: $(POSTGRES_HOST):$(POSTGRES_PORT)/$(POSTGRES_DB)"
	@echo "  Profile: $(SPRING_PROFILES_ACTIVE)"
	@echo ""
	java -jar target/recipe-management-service-*.jar

run-local-debug: ## Run locally with remote debugging on port 5005
	@echo "$(GREEN)Starting Recipe Management Service with debugging...$(NC)"
	@echo "  Database: $(POSTGRES_HOST):$(POSTGRES_PORT)/$(POSTGRES_DB)"
	@echo "  Profile: $(SPRING_PROFILES_ACTIVE)"
	@echo "  Debug port: 5005"
	@echo ""
	mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

build: ## Build the application (skip tests)
	mvn clean package -DskipTests

test: ## Run all tests
	mvn test

test-unit: ## Run unit tests only
	mvn test -Dgroups="unit"

test-component: ## Run component tests only
	mvn test -Dgroups="component"

clean: ## Clean build artifacts
	mvn clean

format: ## Format code with Spotless
	mvn spotless:apply

check: ## Run code quality checks
	mvn checkstyle:check spotbugs:check pmd:check

# =============================================================================
# Testing
# =============================================================================

test-integration: ## Run Karate API integration tests
	mvn test -Dgroups="dependency"

test-performance: ## Run JMeter performance tests
	@echo "$(GREEN)Running JMeter performance tests...$(NC)"
	mvn jmeter:jmeter

test-all: ## Run all tests with full verification
	mvn verify

test-single: ## Run a specific test (usage: make test-single TEST=MyTestClass)
	@if [ -z "$(TEST)" ]; then \
		echo "$(YELLOW)Usage: make test-single TEST=MyTestClass$(NC)"; \
		exit 1; \
	fi
	mvn test -Dtest=$(TEST)

# =============================================================================
# Coverage
# =============================================================================

coverage: ## Run tests and generate coverage report
	@echo "$(GREEN)Running tests with coverage...$(NC)"
	mvn test jacoco:report
	@echo ""
	@echo "$(GREEN)Coverage report generated:$(NC) target/site/jacoco/index.html"

coverage-check: ## Verify coverage meets 85% threshold
	mvn jacoco:check

coverage-open: coverage ## Generate and open coverage report in browser
	@echo "$(GREEN)Opening coverage report...$(NC)"
	@xdg-open target/site/jacoco/index.html 2>/dev/null || open target/site/jacoco/index.html 2>/dev/null || echo "$(YELLOW)Open target/site/jacoco/index.html in your browser$(NC)"

# =============================================================================
# Code Quality
# =============================================================================

format-check: ## Check code formatting without applying
	mvn spotless:check

pom-sort: ## Sort dependencies in pom.xml
	mvn sortpom:sort

check-all: ## Run all code quality checks with detailed output
	@echo "$(GREEN)Running all quality checks...$(NC)"
	mvn checkstyle:check spotbugs:check pmd:check pmd:cpd-check

# =============================================================================
# Build
# =============================================================================

compile: ## Compile source code only
	mvn compile

install: ## Build and install to local Maven repository
	mvn clean install

verify: ## Run full verification (build + all tests + coverage)
	mvn verify

# =============================================================================
# Docker/Kubernetes
# =============================================================================

docker-build: ## Build Docker image locally
	@echo "$(GREEN)Building Docker image...$(NC)"
	docker build -t recipe-management-service:latest .

k8s-deploy: ## Full Kubernetes deployment (Minikube)
	@echo "$(GREEN)Deploying to Kubernetes...$(NC)"
	./scripts/containerManagement/deploy-container.sh

k8s-start: ## Scale Kubernetes deployment to 1 replica
	./scripts/containerManagement/start-container.sh

k8s-stop: ## Scale Kubernetes deployment to 0 replicas
	./scripts/containerManagement/stop-container.sh

k8s-status: ## Check Kubernetes deployment status
	./scripts/containerManagement/get-container-status.sh

k8s-update: ## Rebuild Docker image and trigger rollout
	./scripts/containerManagement/update-container.sh

k8s-cleanup: ## Full Kubernetes cleanup
	./scripts/containerManagement/cleanup-container.sh

k8s-logs: ## View Kubernetes pod logs
	@kubectl logs -l app=recipe-management-service --tail=100 -f

# =============================================================================
# Utilities
# =============================================================================

deps: ## Show dependency tree
	mvn dependency:tree

deps-updates: ## Check for dependency updates
	mvn versions:display-dependency-updates

delombok: ## Generate delomboked source code
	./scripts/sourceManagement/delombok-all.sh

check-test-files: ## Find classes without unit tests
	./scripts/sourceManagement/check-unit-test-file-coverage.sh

# =============================================================================
# Workflows
# =============================================================================

pre-commit: format check test-unit ## Quick pre-commit validation (format + check + unit tests)
	@echo "$(GREEN)Pre-commit checks passed!$(NC)"

ci: clean check-all test coverage ## Full CI pipeline (clean + quality + tests + coverage)
	@echo "$(GREEN)CI pipeline completed successfully!$(NC)"
