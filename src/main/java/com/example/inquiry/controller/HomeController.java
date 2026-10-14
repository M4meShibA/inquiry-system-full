package com.example.inquiry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /** お客様向けのトップページ(ログイン不要) */
    @GetMapping("/")
    public String home() {
        return "home";
    }

    /** 担当者用の入口。未ログインなら、自動でログイン画面に移る */
    @GetMapping("/admin")
    public String admin() {
        return "redirect:/admin/inquiries";
    }
}
