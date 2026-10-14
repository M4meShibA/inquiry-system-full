package com.example.inquiry.controller;

import com.example.inquiry.model.Inquiry;
import com.example.inquiry.model.InquiryStatus;
import com.example.inquiry.service.DraftService;
import com.example.inquiry.service.InquiryService;
import com.example.inquiry.service.MailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

/** 担当者用の画面(要ログイン)。一覧・詳細・ステータス変更・返信・削除 */
@Controller
@RequestMapping("/admin/inquiries")
public class InquiryController {

    private static final int MAX_REPLY_LENGTH = 4000;
    private static final String BASE = "/admin/inquiries";

    private final InquiryService service;
    private final DraftService draftService;
    private final MailService mailService;

    public InquiryController(InquiryService service, DraftService draftService, MailService mailService) {
        this.service = service;
        this.draftService = draftService;
        this.mailService = mailService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("inquiries", service.findAll());
        return "inquiries/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("inquiry", service.get(id));
        model.addAttribute("statuses", InquiryStatus.values());
        return "inquiries/detail";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.delete(id);
        redirectAttributes.addFlashAttribute("info", "問い合わせを削除しました");
        return "redirect:" + BASE;
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id, @RequestParam InquiryStatus status) {
        service.changeStatus(id, status);
        return "redirect:" + BASE + "/" + id;
    }

    /**
     * 返信を保存する。send=true のときは、お客様のメールアドレスにも送信する。
     * メール送信に失敗したときは返信を保存せず、入力した文章を画面に戻す。
     */
    @PostMapping("/{id}/replies")
    public String addReply(@PathVariable Long id,
                           @RequestParam String body,
                           @RequestParam(defaultValue = "false") boolean aiDraft,
                           @RequestParam(defaultValue = "false") boolean send,
                           RedirectAttributes redirectAttributes) {
        String back = "redirect:" + BASE + "/" + id;

        if (body == null || body.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "返信内容を入力してください");
            return back;
        }
        if (body.length() > MAX_REPLY_LENGTH) {
            redirectAttributes.addFlashAttribute("error", "返信は" + MAX_REPLY_LENGTH + "文字以内で入力してください");
            keepDraft(redirectAttributes, body, aiDraft);
            return back;
        }

        LocalDateTime emailedAt = null;
        if (send) {
            try {
                mailService.sendReply(service.get(id), body.trim());
                emailedAt = LocalDateTime.now();
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error",
                        "メールを送れなかったため、返信は保存していません: " + e.getMessage());
                keepDraft(redirectAttributes, body, aiDraft);
                return back;
            }
        }

        service.addReply(id, body, aiDraft, emailedAt);
        redirectAttributes.addFlashAttribute("info",
                send ? "返信を保存し、メールを送信しました" : "返信を保存しました(メールは送信していません)");
        return back;
    }

    @PostMapping("/{id}/draft")
    public String draft(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Inquiry inquiry = service.get(id);
        try {
            keepDraft(redirectAttributes, draftService.createDraft(inquiry), true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            redirectAttributes.addFlashAttribute("error", "下書きの作成が中断されました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "下書きを作れませんでした: " + e.getMessage());
        }
        return "redirect:" + BASE + "/" + id;
    }

    private void keepDraft(RedirectAttributes redirectAttributes, String text, boolean fromAi) {
        redirectAttributes.addFlashAttribute("draft", text);
        redirectAttributes.addFlashAttribute("draftFromAi", fromAi);
    }
}
