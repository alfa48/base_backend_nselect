# Autenticação e Segurança (Spring Security)

Este documento detalha como funciona o sistema de segurança baseado em **Spring Security** integrado com a API externa.

---

## 1. Arquitetura de Segurança
A nossa segurança utiliza o **Spring Security 6+** para gerir o ciclo de vida da autenticação e autorização, eliminando a necessidade de validações manuais nos controllers.

- **Fluxo**: Formulário de Login -> Spring Security -> `CustomAuthenticationProvider` -> API Externa (JWT).
- **Sessão**: O Spring Security gere a sessão e o `SecurityContext`.
- **API Context**: O `BaseApiService` obtém automaticamente o token do contexto de segurança para as chamadas à API.

---

## 2. Configuração Principal (`SecurityConfig`)
A classe `SecurityConfig.java` centraliza as regras de acesso:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/inscricao", "/css/**", "/js/**", "/images/**", "/fonts/**").permitAll() // Rotas Públicas
            .requestMatchers("/utilizadores/**").hasRole("Administrador") // Acesso restrito
            .anyRequest().authenticated() // Tudo o resto exige login
        )
        .formLogin(login -> login
            .loginPage("/")
            .loginProcessingUrl("/autenticar")
            .defaultSuccessUrl("/userLogin", true)
            .usernameParameter("username")
            .passwordParameter("senha")
        );
    return http.build();
}
```

---

## 3. Provedor de Autenticação Customizado
Como não usamos banco de dados local para usuários, implementamos o `CustomAuthenticationProvider`. Ele:
1. Recebe as credenciais do formulário.
2. Chama `AuthService.login(request)` para validar na API externa.
3. Recebe o `UserDTO` com o `access_token`.
4. Converte a Role da API para o formato do Spring (`ROLE_Administrador`).
5. Cria o objeto de Autenticação que fica disponível em toda a app.

---

## 4. Como Proteger Novas Rotas
Não precisas de colocar `if (!authService.isAuthenticated())` nos teus controllers. Basta:

1. Adicionar o prefixo da rota no `SecurityConfig.java`.
2. Usar `.hasRole("NomeDaRole")` ou `.authenticated()`.

Exemplo:
```java
// No SecurityConfig
.requestMatchers("/academia/**").hasRole("Academia")
```

---

## 5. Acesso ao Utilizador Logado
Para obter os dados do utilizador ou o token em qualquer serviço ou controller:

**No Java (via BaseApiService):**
```java
UserDTO user = getSessionUser();
String token = user.getAccessToken();
```

**No HTML (Thymeleaf):**
Podes usar o extras do Spring Security para mostrar/esconder elementos:
```html
<div sec:authorize="hasRole('Administrador')">
    Apenas Admins vêm isto
</div>
```

---

## 6. Remember-Me e UserDetailsService
O sistema está configurado para manter a sessão ativa por n dias(altravel). Para isso, foi implementado o `CustomUserDetailsService`. Como a autenticação é externa, este serviço serve apenas como um marcador para o Spring Security; a revalidação real do token continua a ser feita via API.

---

## 7. Ajustes Temporários e Debugging
Caso a API externa não retorne uma Role definida para o utilizador (campo `role` nulo no JSON), o sistema está configurado **temporariamente** no `CustomAuthenticationProvider` para atribuir a role `Administrador`.

> [!WARNING]
> Este comportamento é temporário para facilitar os testes enquanto a API não é ajustada. Para reverter, basta remover o bloco `else` no método `authenticate` do `CustomAuthenticationProvider.java`.

---

## 8. Troubleshooting
- **Erro 403 (Forbidden)**: Geralmente significa que o utilizador está logado mas não tem a Role necessária definida no `SecurityConfig`.
- **Erro 401 (Unauthorized)**: O token expirou ou é inválido. O `BaseApiService` tenta o refresh automático.
