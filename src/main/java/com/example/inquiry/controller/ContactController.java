package com.example.inquiry.controller;

import com.example.inquiry.form.InquiryForm;
import com.example.inquiry.service.InquiryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** お客様向けの問い合わせフォーム(ログイン不要) */
@Controller
@RequestMapping("/contact")
public class ContactController {

    private final InquiryService service;

    public ContactController(InquiryService service) {
        this.service = service;
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("form", new InquiryForm());
        return "inquiries/form";
    }

    @PostMapping
    public String submit(@Valid @ModelAttribute("form") InquiryForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "inquiries/form";
        }
        service.create(form);
        return "redirect:/contact/thanks";
    }

    @GetMapping("/thanks")
    public String thanks() {
        return "inquiries/thanks";
    }
}
