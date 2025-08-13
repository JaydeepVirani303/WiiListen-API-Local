package com.wiilisten.service;

import com.wiilisten.entity.BookedCalls;

import java.io.IOException;
import java.util.List;

public interface PaymentReportService {
    public List<BookedCalls> getAllPayments();

    public byte[] getReportOfAllPayment() throws IOException;
}
