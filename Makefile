# Makefile for DPD Project

.PHONY: help build up down restart logs ps clean shell test dev

# Alvo padrão: exibe a ajuda
all: help

help: ## Exibe esta lista de comandos disponíveis
	@echo "Ajudada para o Projeto DPD:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-15s\033[0m %s\n", $$1, $$2}'

build: ## Constrói as imagens do Docker definidas no docker-compose
	sudo docker compose build

up: ## Inicia os contentores em segundo plano (background)
	sudo docker compose up -d

down: ## Para e remove os contentores e redes
	sudo docker compose down

restart: ## Reinicia os contentores (down + up)
	sudo docker compose down && sudo docker compose up -d

logs: ## Mostra os logs dos contentores em tempo real
	sudo docker compose logs -f

ps: ## Lista o estado dos contentores
	sudo docker compose ps

clean: ## Limpa artefactos do Maven e remove imagens/volumes do Docker
	mvn clean
	sudo docker compose down --rmi all --volumes --remove-orphans

shell: ## Abre um terminal interativo dentro do contentor da aplicação
	sudo docker compose exec app bash

test: ## Executa os testes unitários dentro do ambiente Docker
	sudo docker compose run --rm app ./mvnw test

dev: ## Executa a aplicação localmente (fora do Docker) para desenvolvimento
	mvn spring-boot:run
