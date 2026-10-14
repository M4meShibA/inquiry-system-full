package com.example.inquiry.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class InquiryForm {

    @NotBlank(message = "お名前を入力してください")
    @Size(max = 100, message = "お名前は100文字以内で入力してください")
    private String customerName;

    @NotBlank(message = "メールアドレスを入力してください")
    @Email(message = "メールアドレスの形式が正しくありません")
    @Size(max = 255, message = "メールアドレスは255文字以内で入力してください")
    private String customerEmail;

    @NotBlank(message = "件名を入力してください")
    @Size(max = 200, message = "件名は200文字以内で入力してください")
    private String subject;

    @NotBlank(message = "本文を入力してください")
    @Size(max = 4000, message = "本文は4000文字以内で入力してください")
    private String body;

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
