package co.ao.base.config.auth;

import co.ao.base.model.UserDTO;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Como a autenticação é via API externa, não conseguimos buscar o usuário apenas pelo username sem a senha.
        // No entanto, o Spring Security exige este bean para o Remember-Me funcionar.
        // Por enquanto, lançamos uma exceção ou retornamos um erro se tentarem carregar sem estarem autenticados.
        throw new UsernameNotFoundException("Busca direta por usuário não suportada nesta arquitetura de API externa.");
    }
}
