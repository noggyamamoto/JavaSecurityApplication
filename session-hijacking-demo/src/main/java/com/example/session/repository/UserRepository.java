package main.java.com.example.session.repository;

import com.example.session.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Busca um usuário pelo nome (usado na autenticação)
    Optional<User> findByUsername(String username);
}