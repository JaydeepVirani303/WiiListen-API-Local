package com.wiilisten.response;

import lombok.Data;

import java.util.Date;

@Data
public class PlanPurchaseDetailResponseDto {
    // Getters & Setters
    private Date purchaseDate;
    private int durationInDays; //1 , 30 , 365
    private String type; //SUBSCRIPTION/ADVERTISEMENT

    // No-args constructor (needed for JSON serialization)
    public PlanPurchaseDetailResponseDto() {
    }

    // All-args constructor
    public PlanPurchaseDetailResponseDto(Date purchaseDate, int durationInDays, String type) {
        this.purchaseDate = purchaseDate;
        this.durationInDays = durationInDays;
        this.type = type;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }

    public void setType(String category) {
        this.type = category;
    }

    // For debugging
    @Override
    public String toString() {
        return "PlanPurchaseDetailResponseDto{" +
                "purchaseDate=" + purchaseDate +
                ", durationInDays=" + durationInDays +
                ", type='" + type + '\'' +
                '}';
    }
}
