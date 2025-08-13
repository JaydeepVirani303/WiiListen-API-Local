package com.wiilisten.service.impl;

import com.wiilisten.entity.BookedCalls;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.request.CombinedListenerUserDTO;
import com.wiilisten.request.CombinedPaymentListenerUserDTO;
import com.wiilisten.service.PaymentReportService;
import com.wiilisten.utils.ExcelGenerator;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PaymentReportServiceImpl extends BaseServiceImpl<ListenerProfile, Long> implements PaymentReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentReportServiceImpl.class);

    private final ExcelGenerator excelGenerator;

    public PaymentReportServiceImpl(ExcelGenerator excelGenerator) {
        this.excelGenerator = excelGenerator;
    }


    /**
     * Initializes the base repository for this service after bean creation.
     */
    @PostConstruct
    public void setBaseRepository() {
        super.baseRepository = getDaoFactory().getListenerProfileRepository();
        LOGGER.info("Base repository for ListenerReportServiceImpl initialized.");
    }

    @Override
    public List<BookedCalls> getAllPayments() {
        LOGGER.info("Fetching all payments from the repository...");
        List<BookedCalls> payments = getDaoFactory()
                .getBookedCallsRepository()
                .findAll();
        LOGGER.info("Fetched {} payment profiles from the database.", payments.size());
        return payments;
    }

    @Override
    public byte[] getReportOfAllPayment() throws IOException {
        LOGGER.info("Starting generation of Listener Report Excel file...");

        // Step 1: Fetch listener profiles
        List<BookedCalls> payments = getAllPayments();

        // Step 2: Map to DTOs
        LOGGER.info("Mapping {} payment to CombinedPaymentUserDTO objects...", payments.size());
        List<CombinedPaymentListenerUserDTO> dtoList = payments.stream()
                .map(CombinedPaymentListenerUserDTO::toDTO)
                .toList();
        LOGGER.info("Mapped {} payments to DTOs successfully.", dtoList.size());

        // Step 3: Generate Excel file
        LOGGER.info("Generating Excel file for {} payments records...", dtoList.size());
        byte[] excelBytes = excelGenerator.generateExcelReport(dtoList);
        LOGGER.info("Payment Report Excel file generated successfully. File size: {} bytes.", excelBytes.length);

        return excelBytes;
    }
}
