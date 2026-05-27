package main.java.com.example.session.config;

import com.example.session.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Filtro de segurança – configura acesso a URLs, login, logout e gerenciamento de sessão.
     * Esta versão é VULNERÁVEL: não regenera o ID da sessão após login.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Regras de autorização
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/h2-console/**", "/register", "/css/**", "/webjars/**").permitAll()
                .anyRequest().authenticated()   // qualquer outra página exige autenticação
            )
            // Permite frames (necessário para o console H2)
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            // Configuração do formulário de login
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/profile", true)
                .permitAll()
            )
            // Logout
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            // ====================================================
            // VULNERABILIDADE: sessionFixation().none() não regenera o ID da sessão.
            // Isso permite Session Fixation (atacante planta um ID antes do login).
            // ====================================================
            .sessionManagement(session -> session
                .sessionFixation().none()
            );
        return http.build();
    }

    /**
     * PasswordEncoder que NÃO codifica senhas (texto puro).
     * INSEGURO – usado apenas para simplificar a demonstração.
     * Em produção, use BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}