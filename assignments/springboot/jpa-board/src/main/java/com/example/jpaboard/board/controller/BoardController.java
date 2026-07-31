package com.example.jpaboard.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BoardController {

    @GetMapping("/")
    public String boardList() {
        return "board-list";
    }

    @GetMapping("/write")
    public String write() {
        return "board-write";
    }

    @GetMapping("/detail")
    public String boardDetail(
            @RequestParam Long id,
            Model model
    ) {
        model.addAttribute("id", id);

        return "board-detail";
    }

    @GetMapping("/update/{id}")
    public String update(
            @PathVariable long id,
            Model model
    ) {
        model.addAttribute("id", id);

        return "board-update";
    }
}