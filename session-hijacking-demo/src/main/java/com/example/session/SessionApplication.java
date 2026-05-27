package main.java.com.example.session;

import com.example.session.model.User;
import com.example.session.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SessionApplication {

    public static void main(String[] args) {
        SpringApplication.run(SessionApplication.class, args);
    }

    /**
     * CommandLineRunner executa ao iniciar a aplicação.
     * Cria um usuário admin e um usuário comum padrão, caso não existam.
     * As senhas são codificadas com o PasswordEncoder injetado.
     */
    @Bean
    public CommandLineRunner init(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                userRepository.save(new User("admin", encoder.encode("admin123"), "ADMIN"));
                System.out.println("Usuário admin criado: admin / admin123");
            }
            if (userRepository.findByUsername("user").isEmpty()) {
                userRepository.save(new User("user", encoder.encode("user123"), "USER"));
                System.out.println("Usuário user criado: user / user123");
            }
        };
    }
}