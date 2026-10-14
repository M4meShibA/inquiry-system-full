package com.example.inquiry.service;

import com.example.inquiry.model.Inquiry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * お客様への返信メールを送るサービス(Gmail SMTP)。
 * 送信元はログインに使うGmailアドレスになる(Gmailの仕様)。
 */
@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final String username;
    private final String password;

    public MailService(JavaMailSender mailSender,
                       @Value("${spring.mail.username:}") String username,
                       @Value("${spring.mail.password:}") String password) {
        this.mailSender = mailSender;
        this.username = username;
        this.password = password;
    }

    public void sendReply(Inquiry inquiry, String body) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalStateException("環境変数 MAIL_USERNAME と MAIL_PASSWORD が設定されていません");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(inquiry.getCustomerEmail());
        // 改行が混ざるとメールヘッダーの不正な書き換えにつながるため、件名の改行は取り除く
        message.setSubject("Re: " + inquiry.getSubject().replaceAll("[\\r\\n]+", " "));
        message.setText(body);

        try {
            mailSender.send(message);
        } catch (MailAuthenticationException e) {
            throw new IllegalStateException(
                    "Gmailの認証に失敗しました。アプリパスワードとメールアドレスを確認してください");
        } catch (MailException e) {
            throw new IllegalStateException("メールを送信できませんでした: " + e.getMessage());
        }
    }
}
