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
                auth.requestMatchers("/utilizadores", "/utilizadores/**").hasRole("Administrador");
                auth.anyRequest().authenticated();
            })
            .formLogin(login -> login
                .loginPage("/")
                .loginProcessingUrl("/autenticar")
                .defaultSuccessUrl("/userLogin", true)
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
                .key("mySecret!")
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
}
