package com.example.inquiry.model;

public enum InquiryStatus {
    NEW("未対応"),
    IN_PROGRESS("対応中"),
    DONE("完了");

    private final String label;

    InquiryStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
