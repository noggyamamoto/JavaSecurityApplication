package main.java.com.example.session.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    // Página de perfil do usuário autenticado
    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "profile";
    }

    // Página administrativa – só acessível para ROLE_ADMIN
    @GetMapping("/admin")
    public String adminPanel(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "admin";
    }
}