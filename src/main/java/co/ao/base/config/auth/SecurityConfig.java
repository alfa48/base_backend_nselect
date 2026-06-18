package co.ao.base.config.auth;

import co.ao.base.service.api.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(customAuthenticationProvider)
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/", "/inscricao", "/css/**", "/js/**", "/images/**", "/fonts/**").permitAll();
                auth.requestMatchers("/admin/**").hasRole("ADMIN");
                auth.requestMatchers("/parceiro/**").hasRole("PARCEIRO");
                auth.anyRequest().authenticated();
            })
            .formLogin(login -> login
                .loginPage("/")
                .loginProcessingUrl("/autenticar")
                .successHandler((request, response, authentication) -> {
                    // Popular a sessão HTTP com os tokens para o BaseApiService
                    if (authentication.getPrincipal() instanceof co.ao.base.model.UserDTO userDTO) {
                        if (userDTO.getAccessToken() != null) {
                            request.getSession().setAttribute("token", userDTO.getAccessToken());
                        }
                        if (userDTO.getRefreshToken() != null) {
                            request.getSession().setAttribute("refreshToken", userDTO.getRefreshToken());
                        }
                        request.getSession().setAttribute("user", userDTO);
                    }
                    response.sendRedirect(request.getContextPath() + "/userLogin");
                })
                .failureUrl("/noauth")
                .usernameParameter("username")
                .passwordParameter("senha")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .rememberMe(me -> me
                .key("mySecret!KeyDpdApi2026#.")
                .tokenValiditySeconds(2592000) // 30 dias
                .rememberMeParameter("checkRememberMe")
                .userDetailsService(customUserDetailsService)
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**");
    }
}
