# Constantes e Configurações

Este documento explica como gerir as configurações globais do projeto e onde encontrar as constantes.

---

## 1. O Ficheiro `Constant.java`
Localizado em `co.ao.base.util.Constant.java`, este ficheiro centraliza as chaves e URLs da API.

### Configurações Atuais:
- **`BASE_URL`**: O endereço da API externa. Alterna automaticamente entre DEV e PROD baseado no perfil do Spring.
- **`API_KEY`**: A chave de segurança para acesso à API.

---

## 2. Variáveis de Ambiente (Recomendado)
Para maior segurança, especialmente no **GitHub** ou **Docker**, deves definir as configurações via variáveis de ambiente. O projeto já está preparado para ler estas variáveis:

| Variável | Descrição | Valor no Properties (Default) |
| :--- | :--- | :--- |
| `API_KEY` | Chave de Acesso à API | `api.key` |
| `API_BASE_URL` | URL Base da API | `api.base-url` |

Podes defini-las no teu ficheiro `.bashrc`, no Docker Compose ou diretamente no IntelliJ/Eclipse.

---

## 3. Perfis (Spring Profiles)
O projeto está preparado para ambientes diferentes.

- **DEV (Padrão)**: Usa URLs de teste.
- **PROD**: Usa URLs de produção.

Podes mudar o perfil no ficheiro `src/main/resources/application.properties`:
```properties
spring.profiles.active=dev
```

---

## 4. Adicionar Novas Constantes
Se precisares de adicionar uma constante global (ex: URL de um serviço externo):
1. Adiciona o valor no `application.properties`.
2. Cria o campo no `Constant.java` com a anotação `@Value`.

---

## 4. Porquê usar Constantes?
- **Fácil Manutenção**: Se a URL da API mudar, só precisas de alterar um ficheiro, não 50 serviços diferentes.
- **Segurança**: Evita espalhar chaves de API pelo código.
