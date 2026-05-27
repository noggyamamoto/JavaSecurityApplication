package main.java.com.example.session.controller;

import com.example.session.model.User;
import com.example.session.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Exibe o formulário de login (Spring Security já processa o POST /login)
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Exibe o formulário de registro
    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    // Processa o registro de um novo usuário
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam(defaultValue = "USER") String role) {
        // Verifica se o usuário já existe
        if (userRepository.findByUsername(username).isPresent()) {
            return "redirect:/login?error=Usuário já existe";
        }
        // Cria e salva o novo usuário (senha codificada)
        User user = new User(username, passwordEncoder.encode(password), role.toUpperCase());
        userRepository.save(user);
        return "redirect:/login?registered";
    }
}