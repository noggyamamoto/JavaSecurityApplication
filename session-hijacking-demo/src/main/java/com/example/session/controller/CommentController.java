package com.example.session.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CommentController {
    private List<String> comments = new ArrayList<>();

    @GetMapping("/comments")
    public String showComments(Model model) {
        model.addAttribute("comments", comments);
        return "comments";
    }

    @PostMapping("/comments")
    public String addComment(@RequestParam("comment") String comment, Model model) {
        comments.add(comment);
        model.addAttribute("comments", comments);
        return "comments";
    }
}
