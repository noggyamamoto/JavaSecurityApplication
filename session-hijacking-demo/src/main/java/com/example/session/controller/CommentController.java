package main.java.com.example.session.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CommentController {

    // Lista simples para armazenar comentários (simula um banco)
    private List<String> comments = new ArrayList<>();

    // Exibe a página de comentários, passando a lista para o template
    @GetMapping("/comments")
    public String showComments(Model model) {
        model.addAttribute("comments", comments);
        return "comments";
    }

    /**
     * Adiciona um comentário. NENHUMA SANITIZAÇÃO é aplicada.
     * Isso permite XSS: se um usuário malicioso enviar <script>...</script>,
     * ele será renderizado como HTML puro quando a página for carregada.
     * (Vulnerabilidade de XSS armazenado)
     */
    @PostMapping("/comments")
    public String addComment(@RequestParam("comment") String comment, Model model) {
        comments.add(comment);   // ❌ armazena o comentário bruto
        model.addAttribute("comments", comments);
        return "comments";
    }
}