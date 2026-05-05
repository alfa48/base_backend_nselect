# DPD - Portal de Gestão (BFF)

Este projeto é o **Backend for Frontend (BFF)** do DPD, desenvolvido em Spring Boot. Ele atua como orquestrador e interface web para a gestão de parceiros, leads, tickets e materiais de apoio.

## Arquitetura

A aplicação funciona como um orquestrador de interface e **não possui base de dados local**. 
- Todas as operações de leitura e escrita são delegadas para uma API externa.
- A segurança de sessão e o fluxo de autenticação são geridos localmente utilizando Spring Security, comunicando com os serviços da API para validação.
- A interface de utilizador é renderizada server-side utilizando **Thymeleaf**, com um sistema de layouts e fragmentos para consistência visual.

## Principais Módulos

- **Dashboard**: Visão geral e estatísticas.
- **Gestão de Parceiros (Analytics)**: Listagem, criação, edição e visualização de parceiros (agentes, etc) com filtros por província e tipo.
- **Gestão de Leads**: Acompanhamento e gestão de oportunidades de negócio.
- **Gestão de Tickets**: Sistema de suporte e acompanhamento de incidências.
- **Materiais de Apoio**: Gestão de recursos disponibilizados aos parceiros.

## Pré-requisitos e Configuração

Certifique-se de ter o Docker e Docker Compose instalados, ou o Java 17+ e Maven para desenvolvimento local.

As configurações principais encontram-se em `src/main/resources/application.properties` (ou `application-dev.properties`). Pode ser necessário configurar a `API_BASE_URL` ou outras variáveis de ambiente dependendo do ambiente.

## Comandos de Inicialização Rápida

O projeto utiliza um `Makefile` para simplificar a gestão de processos:

```bash
make build    # Constrói o ambiente (Docker)
make up       # Inicia a aplicação no Docker (Porta 8091 por padrão)
make logs     # Visualiza os logs da aplicação a correr no Docker
make dev      # Executa localmente com Hot-Reload (recomendado para desenvolvimento)
```

## Estrutura do Projeto

- `src/main/java/co/ao/base/controller/`: Controladores web (Thymeleaf) e API controllers (REST).
- `src/main/java/co/ao/base/service/`: Serviços responsáveis pela comunicação com as APIs externas.
- `src/main/resources/templates/`: Ficheiros HTML/Thymeleaf organizados por módulos (admin, auth, etc).
- `src/main/resources/static/`: Recursos estáticos como CSS, JS e imagens.
