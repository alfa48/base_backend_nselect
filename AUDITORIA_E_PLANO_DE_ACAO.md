# Auditoria Completa e Plano de Ação: Dexa Parceiros

## Resumo Executivo

| Categoria | Críticos | Altos | Médios | Baixos |
| --------- | -------- | ----- | ------ | ------ |
| Arquitetura | 0 | 2 | 2 | 0 |
| Segurança | 2 | 1 | 0 | 0 |
| Frontend / HTML / CSS | 0 | 1 | 2 | 1 |
| Regras de Negócio | 0 | 1 | 0 | 0 |
| Testes | 1 | 0 | 0 | 0 |

---

## 1. Segurança

### ID: SEC-001 (CRÍTICO) - Elevação Automática de Privilégios
**Arquivo:** `src/main/java/co/ao/base/config/auth/CustomAuthenticationProvider.java`
**Descrição:** O sistema contém um "ajuste temporário" que eleva qualquer utilizador a Administrador (`ROLE_ADMIN`) caso a API externa não devolva uma `role` explícita no payload de login.
**Impacto:** Risco altíssimo. Se a API falhar em devolver a role, utilizadores normais ganham acesso de administrador.

#### 🛠️ Plano de Ação (Passo-a-Passo):
1. Abrir `CustomAuthenticationProvider.java`.
2. Localizar a linha ~42 onde diz `// AJUSTE TEMPORÁRIO: Se a API não devolver role, assumimos Administrador para testes`.
3. Substituir o bloco `else { authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN")); }` pelo lançamento de uma exceção:
   ```java
   else {
       throw new BadCredentialsException("Acesso negado: Perfil de utilizador não definido.");
   }
   ```
4. Testar o fluxo de login com um utilizador inválido para garantir que não consegue entrar no sistema.

### ID: SEC-002 (CRÍTICO) - CSRF Desativado em Aplicação Baseada em Sessão
**Arquivo:** `src/main/java/co/ao/base/config/auth/SecurityConfig.java`
**Descrição:** O CSRF está explicitamente desativado (`csrf.disable()`), mas a aplicação usa sessões (`JSESSIONID`) geridas pelo Spring Security.
**Impacto:** Permite ataques de *Cross-Site Request Forgery*, possibilitando que scripts maliciosos tomem ações destrutivas em nome do parceiro autenticado.

#### 🛠️ Plano de Ação (Passo-a-Passo):
1. Abrir `SecurityConfig.java`.
2. Remover a linha `.csrf(csrf -> csrf.disable())`.
3. Inserir proteção de CSRF e expor o token nos cookies para o Angular/JS poder consumir facilmente:
   ```java
   .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
   ```
4. Em cada `fetch()` de POST/PUT/DELETE nos ficheiros `.html` (ex: `editar-lead---parceiro.html`), alterar o código JavaScript para ler o cookie `XSRF-TOKEN` e anexá-lo aos cabeçalhos (Headers) como `X-XSRF-TOKEN`.

---

## 2. Arquitetura e Backend (Java)

### ID: ARQ-001 (ALTO) - Tratamento Genérico de Exceções nas APIs
**Arquivo:** Controllers em `src/main/java/co/ao/base/controller/api/`
**Descrição:** Os controllers locais capturam `Exception` genérica e respondem de forma oculta.

#### 🛠️ Plano de Ação (Passo-a-Passo):
1. Criar um novo ficheiro: `src/main/java/co/ao/base/controller/api/GlobalExceptionHandler.java`.
2. Anotar a classe com `@RestControllerAdvice`.
3. Implementar métodos para gerir `HttpStatusCodeException` (erros da API) e enviar o corpo original de volta para o cliente.
   ```java
   @ExceptionHandler(HttpStatusCodeException.class)
   public ResponseEntity<?> handleApiException(HttpStatusCodeException e) {
       return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
   }
   ```
4. Limpar os `try/catch` genéricos dos controllers, deixando-os delegar os erros.

### ID: ARQ-002 (ALTO) - Ausência de Timeout no RestTemplate
**Arquivo:** Configuração de Injeção de Dependências
**Descrição:** A falta de Timeout pode causar a falha (*thread starvation*) da aplicação parceira se a API principal (`api-parceiros.dexa-contas.com`) estiver lenta.

#### 🛠️ Plano de Ação (Passo-a-Passo):
1. Criar `RestTemplateConfig.java` em `co.ao.base.config`.
2. Definir o bean com tempos curtos (ex: 5s conexão / 15s leitura):
   ```java
   @Bean
   public RestTemplate restTemplate(RestTemplateBuilder builder) {
       return builder
           .setConnectTimeout(Duration.ofSeconds(5))
           .setReadTimeout(Duration.ofSeconds(15))
           .build();
   }
   ```

### ID: ARQ-003 (MÉDIO) - Race Condition na Renovação de Tokens
**Arquivo:** `BaseApiService.java` (método `tryRefreshToken`)
**Descrição:** Múltiplos chamados concorrentes invalidarão a sessão acidentalmente.

#### 🛠️ Plano de Ação (Passo-a-Passo):
1. Em `BaseApiService.java`, adicionar a keyword `synchronized` ao método:
   `protected synchronized boolean tryRefreshToken() { ... }`
2. Esta é a solução mais rápida para mitigar falhas na concorrência de sessão, embora para o longo prazo se recomende a implementação de Spring Security OAuth2.

---

## 3. Frontend (Thymeleaf, HTML, JavaScript)

### ID: FRT-001 (ALTO) - Forte Acoplamento JavaScript Inline
**Arquivo:** Todos os `*.html` da interface.
**Descrição:** Centenas de linhas de código `fetch` no final dos HTMLs.

#### 🛠️ Plano de Ação (Passo-a-Passo):
1. Criar `src/main/resources/static/js/api-client.js`.
2. Criar funções globais como `window.apiPost(url, data)` que já incluem a gestão de CSRF e *Modals* de "A carregar".
3. Remover as tags `<script>` repetitivas do `editar-lead---parceiro.html` e usar chamadas limpas.

### ID: FRT-002 (MÉDIO) - Repetição Massiva de HTML
**Arquivo:** `criar-lead---parceiro.html` e `editar-lead---parceiro.html`

#### 🛠️ Plano de Ação (Passo-a-Passo):
1. Criar `src/main/resources/templates/layout/fragments/lead-form.html`.
2. Extrair `<form class="criar-content-wrapper">` do ficheiro.
3. Nas páginas de criar e editar, incluir via `<div th:replace="~{layout/fragments/lead-form :: form}"></div>`.

---

## 4. Testes e Qualidade

### ID: TST-001 (CRÍTICO) - Ausência de Cobertura de Testes
**Arquivo:** Faltam testes em `src/test/java/co/ao/base/`
**Descrição:** O sistema quebra sem aviso.

#### 🛠️ Plano de Ação (Passo-a-Passo):
1. Adicionar `spring-boot-starter-test` no `pom.xml`.
2. Criar `AuthServiceTest.java`.
3. Utilizar `@MockBean` para simular o `RestTemplate` e testar se `CustomAuthenticationProvider` bloqueia logins com roles nulas.

---

## Score Final

| Dimensão | Nota |
| :--- | :---: |
| **Arquitetura** | **6.0** | 
| **Segurança** | **4.0** |
| **Performance** | **7.5** | 
| **Qualidade do Código** | **5.5** |
| **Frontend** | **6.0** | 
| **Testes** | **1.0** | 

### Conclusão e Próximo Passo
Recomendamos a execução imediata das tarefas classificadas como **CRÍTICO** (Sec-001 e Sec-002) num ambiente isolado. Para tal, deve começar pelo **Passo 1 do SEC-001**, retificando o *Authentication Provider*.
