# Spring Boot BFF Starter - App Base

Este projeto é a **App Base oficial** para o desenvolvimento de novas aplicações na nossa stack. Ele não é apenas um projeto para ser expandido, mas sim o **esqueleto (seed/skeleton)** que deve ser clonado e utilizado como ponto de partida para toda e qualquer nova aplicação que siga o padrão BFF (Backend for Frontend).

## Objetivo da App Base

Providenciar uma estrutura pré-configurada e padronizada para que novos programadores não percam tempo configurando:
1.  **Infraestrutura Docker**: Makefile e Docker Compose prontos.
2.  **Arquitetura de Consumo**: Serviços genéricos para APIs externas.
3.  **UI Componentizada**: Sistema de fragmentos Thymeleaf e layout responsivo.
4.  **Segurança de Sessão**: Fluxo de autenticação manual pré-implementado.

---

## 🛠 Como iniciar uma nova aplicação a partir desta Base

1.  **Clone este repositório** como o seu novo projeto.
2.  **Renomeie o artefacto** no `pom.xml` (`<artifactId>`).
3.  **Configure as constantes**: Defina as chaves de API e URLs no ficheiro `application.properties` ou via variáveis de ambiente (`API_KEY` e `API_BASE_URL`).
4.  **Desenvolva seus módulos**: Siga os padrões descritos no [DEV_DOC.md](DEV_DOC.md).

---

## Arquitetura Base (Consumo Externo)

A aplicação funciona como um orquestrador de interface. Ela **não possui base de dados local**. 
- Todas as operações de leitura e escrita devem ser delegadas para APIs via `BaseApiService`.
- A lógica de negócio "pesada" reside nos microserviços externos.

---

## Componentes Reutilizáveis

- **`BaseApiService`**: Herança obrigatória para novos serviços de API.
- **`Constant`**: Centralizador de configurações sensíveis (agora via Properties/Env).
- **`Fragments`**: Localizados em `templates/layout/fragments/`, garantem que o "look and feel" da marca seja consistente em todas as aplicações derivadas.

---

## Comandos de Inicialização Rápida

```bash
make build    # Constrói o ambiente (Docker)
make up       # Inicia a aplicação (Porta 8090)
- `make logs`: Monitorização técnica
- `make dev`: Executa localmente com Hot-Reload (mais rápido para dev)
```

---

## Notas de Desenvolvimento
Para mais detalhes técnicos sobre como implementar novos módulos, consulte o arquivo [DEV_DOC.md](DEV_DOC.md).
