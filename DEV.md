# 🚀 Roadmap de Desenvolvimento - Fiinika Comercial

Bem-vindo ao centro de documentação do projeto! Este documento serve como o teu mapa para navegar e desenvolver nesta aplicação. Segue os links abaixo para aprenderes cada parte do sistema.

---

## 🛠️ 1. Começar o Projeto
Se acabaste de chegar ou queres usar este projeto como base para algo novo.
- [**Configuração Inicial (Setup)**](documentation/SETUP.md): Como preparar o ambiente e correr a app.

---

## 🔐 2. Segurança e Acesso
Como garantimos que apenas quem deve entrar, entra.
- [**Autenticação e Login**](documentation/AUTH.md): Fluxo de tokens JWT e implementação de login.

---

## 📡 3. Comunicação com a API
Como falar com o mundo exterior (Backend API).
- [**Criar Serviços de API**](documentation/API_SERVICE.md): Como usar o `BaseApiService` para chamadas automáticas.
- [**Gestão de Logs**](documentation/LOGS.md): Como ver (ou esconder) o que a API está a responder.

---

## 🎨 4. Frontend e Interface (Thymeleaf)
Como levar os dados do Java para o utilizador e vice-versa.
- [**Thymeleaf (Java <-> HTML)**](documentation/THYMELEAF.md): Vínculos de dados, formulários e fragmentos.
- [**Implementação de Paginação**](documentation/PAGINATION.md): Como gerir páginas entre UI (1-based) e API (0-based).
- [**Uso de Modais**](documentation/MODALS.md): Avisos de sucesso, erro e confirmação.

---

## ⚙️ 5. Configurações Globais
Onde estão guardadas as chaves e URLs.
- [**Constantes e Configurações**](documentation/CONSTANTS.md): Uso do ficheiro `Constant.java` e perfis de ambiente.

---

## 💡 Dica de Produtividade
Este projeto usa **Hot-Reload**. Sempre que gravares um ficheiro Java, CSS ou HTML, a aplicação atualiza-se sozinha. Não percas tempo a reiniciar o servidor manualmente!
