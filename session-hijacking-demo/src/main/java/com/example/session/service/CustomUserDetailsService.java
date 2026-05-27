package main.java.com.example.session.service;

import com.example.session.model.User;
import com.example.session.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Método obrigatório do Spring Security. Converte um usuário do banco
     * em um objeto UserDetails (com nome, senha e autoridades).
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        // Converte a role (ex: "ADMIN") para "ROLE_ADMIN" (padrão Spring Security)
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());

        // Retorna o UserDetails com senha em texto puro (apenas para demo – inseguro)
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
}