package com.example.inquiry.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "replies")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inquiry_id")
    private Inquiry inquiry;

    @Column(nullable = false, length = 4000)
    private String body;

    @Column(nullable = false)
    private boolean aiDraft;

    /** お客様にメール送信した日時。送信していない返信(メモのみ)は null */
    private LocalDateTime emailedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Reply() {
    }

    public Reply(Inquiry inquiry, String body, boolean aiDraft, LocalDateTime emailedAt) {
        this.inquiry = inquiry;
        this.body = body;
        this.aiDraft = aiDraft;
        this.emailedAt = emailedAt;
    }

    public Long getId() { return id; }
    public Inquiry getInquiry() { return inquiry; }
    public String getBody() { return body; }
    public boolean isAiDraft() { return aiDraft; }
    public LocalDateTime getEmailedAt() { return emailedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
