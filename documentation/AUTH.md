# Autenticação e Login

Este documento detalha como funciona o sistema de login e como deves implementar fluxos de autenticação seguindo o nosso padrão.

---

## 1. Fluxo de Autenticação (JWT)
A nossa autenticação baseia-se em Tokens JWT. 
- Quando fazes login, a API retorna um `access_token` e um `refresh_token`.
- O `BaseApiService` guarda e envia estes tokens automaticamente em todas as chamadas.

---

## 2. Como Implementar o Login (Passo a Passo)

### A. O Modelo de Pedido (`LoginRequest`)
Cria uma classe simples para os dados que vêm do formulário:
```java
@Data
public class LoginRequest {
    private String username;
    private String password;
}
```

### B. O Serviço (`AuthService`)
O serviço deve herdar de `BaseApiService` e gerir a sessão:
```java
@Service
public class AuthService extends BaseApiService {
    public UserDTO login(LoginRequest request) {
        UserDTO response = post("/auth/login", request, UserDTO.class);
        if (response != null && response.getAccessToken() != null) {
            session.setAttribute("user", response);
            session.setAttribute("token", response.getAccessToken());
            session.setAttribute("refreshToken", response.getRefreshToken());
        }
        return response;
    }
}
```

### C. O Controller de Visualização
Gere a exibição da página de login e protege a rota:
```java
@GetMapping("/")
public String index(Model model) {
    if (authService.isAuthenticated()) return "redirect:/dashboard";
    model.addAttribute("loginRequest", new LoginRequest());
    return "index";
}
```

---

## 3. Verificação de Autenticação
Para verificar se um utilizador está ligado em qualquer parte do código Java:
```java
if (authService.isAuthenticated()) {
    // Código para utilizador logado
}
```

---

## 4. Renovação de Token (Auto-Refresh)
Já temos implementado no `BaseApiService` um sistema que:
1. Detecta se o token expirou (Erro 401 ou 403).
2. Tenta renovar o token usando o `refresh_token` sem que o utilizador perceba.
3. Repete o pedido original com o novo token.
