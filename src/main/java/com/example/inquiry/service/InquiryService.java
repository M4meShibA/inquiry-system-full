package com.example.inquiry.service;

import com.example.inquiry.form.InquiryForm;
import com.example.inquiry.model.Inquiry;
import com.example.inquiry.model.InquiryStatus;
import com.example.inquiry.model.Reply;
import com.example.inquiry.repository.InquiryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    public InquiryService(InquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }

    public List<Inquiry> findAll() {
        return inquiryRepository.findAllByOrderByCreatedAtDesc();
    }

    public Inquiry get(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "問い合わせが見つかりません: " + id));
    }

    @Transactional
    public Inquiry create(InquiryForm form) {
        Inquiry inquiry = new Inquiry();
        inquiry.setCustomerName(form.getCustomerName().trim());
        inquiry.setCustomerEmail(form.getCustomerEmail().trim());
        inquiry.setSubject(form.getSubject().trim());
        inquiry.setBody(form.getBody().trim());
        return inquiryRepository.save(inquiry);
    }

    @Transactional
    public void changeStatus(Long id, InquiryStatus status) {
        Inquiry inquiry = get(id);
        inquiry.setStatus(status);
    }

    @Transactional
    public void addReply(Long id, String body, boolean aiDraft, LocalDateTime emailedAt) {
        Inquiry inquiry = get(id);
        inquiry.getReplies().add(new Reply(inquiry, body.trim(), aiDraft, emailedAt));
        inquiry.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void delete(Long id) {
        Inquiry inquiry = get(id);
        inquiryRepository.delete(inquiry); // 対応履歴(replies)も一緒に削除される
    }
}
