# Configuração Inicial (Setup)

Este documento explica como preparar este projeto como base para qualquer nova aplicação backend utilizando a nossa arquitetura.

---

## 1. Requisitos Prévios
Certifica-te de que tens as seguintes ferramentas instaladas:
- **Java 17** ou superior.
- **Maven 3.8+**.
- **Docker e Docker Compose** (Opcional, mas recomendado para a base de dados).

---

## 2. Como Baixar e Preparar
Para usares este projeto como base:

1. **Clonar/Copiar o Repositório**:
   Copia todos os ficheiros para a tua nova pasta de projeto.

2. **Limpar Artefactos Antigos**:
   No terminal, corre:
   ```bash
   mvn clean
   ```

3. **Configurar o Nome do Projeto (Opcional)**:
   Altera o nome do projeto no ficheiro `pom.xml` na tag `<name>` e `<description>`.

---

## 3. Comandos Essenciais (Makefile)
Usamos um `Makefile` para simplificar as tarefas diárias. Não precisas de decorar comandos complexos do Maven ou Docker.

| Comando | Descrição |
| :--- | :--- |
| `make dev` | **O mais importante.** Executa a aplicação localmente com Hot-Reload. |
| `make build` | Constrói a imagem Docker da aplicação. |
| `make up` | Sobe os contentores (Base de dados, etc.) via Docker Compose. |
| `make down` | Para todos os serviços do Docker. |
| `make clean` | Limpa a pasta `target` e artefactos do Maven. |

---

## 4. Estrutura de Pastas
- `src/main/java`: Todo o código Java (Controllers, Services, Models).
- `src/main/resources/templates`: Ficheiros HTML (Thymeleaf).
- `src/main/resources/static`: Ficheiros estáticos (CSS, JS, Imagens).
- `src/main/resources/application.properties`: Configurações globais.

---

## 5. Verificação
Após correr `make dev`, a aplicação deverá estar disponível em:
`http://localhost:8091` (ou a porta configurada no `application.properties`).
