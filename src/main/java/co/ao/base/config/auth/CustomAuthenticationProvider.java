package co.ao.base.config.auth;

import co.ao.base.model.LoginRequest;
import co.ao.base.model.UserDTO;
import co.ao.base.service.api.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AuthService authService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            LoginRequest loginRequest = new LoginRequest(username, password);
            UserDTO userDTO = authService.login(loginRequest);

            if (userDTO != null && userDTO.getAccessToken() != null) {
                List<GrantedAuthority> authorities = new ArrayList<>();
                if (userDTO.getRole() != null) {
                    // Standardize role to uppercase to match security config and controller checks
                    String role = userDTO.getRole().toUpperCase();
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                } else {
                    // AJUSTE TEMPORÁRIO: Se a API não devolver role, assumimos Administrador para testes
                    System.out.println("DEBUG SECURITY: Role ausente na API. Atribuindo Administrador temporariamente.");
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }

                // Return authenticated token
                return new UsernamePasswordAuthenticationToken(userDTO, password, authorities);
            } else {
                throw new BadCredentialsException("Falha na autenticação com a API.");
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Erro ao conectar com o serviço de autenticação: " + e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
