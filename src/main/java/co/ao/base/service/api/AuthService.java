package co.ao.base.service.api;

import co.ao.base.model.LoginRequest;
import co.ao.base.model.SignupRequest;
import co.ao.base.model.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class AuthService extends BaseApiService {

    public UserDTO login(LoginRequest request) {
        // Caminho relativo ao BaseURL
        UserDTO response = post("/auth/login", request, UserDTO.class);
        
        if (response != null && response.getAccessToken() != null) {
            authenticateUser(response);
        }
            
        return response;
    }

    public UserDTO register(SignupRequest request) {
        // Caminho relativo ao BaseURL (/auth/register)
        UserDTO response = post("/auth/register", request, UserDTO.class);
        
        if (response != null && response.getAccessToken() != null) {
            // Login automático após registo bem-sucedido
            authenticateUser(response);
        }
            
        return response;
    }

    private void authenticateUser(UserDTO user) {
        session.setAttribute("user", user);
        session.setAttribute("token", user.getAccessToken());
        session.setAttribute("refreshToken", user.getRefreshToken());
    }

    public void logout() {
        try {
            post("/auth/logout", null, Void.class);
        } catch (Exception e) {
            // Log do erro
        }
        session.removeAttribute("user");
        session.removeAttribute("token");
        session.removeAttribute("refreshToken");
        session.invalidate();
    }

    public boolean isAuthenticated() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);
    }
}
