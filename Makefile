# Makefile for recipe-management-service
# Local development commands

.PHONY: help run-local run-local-jar run-local-debug build test clean format check

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
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' Makefile | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(YELLOW)%-20s$(NC) %s\n", $$1, $$2}'
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
