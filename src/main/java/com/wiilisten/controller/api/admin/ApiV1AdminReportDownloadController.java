package com.wiilisten.controller.api.admin;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.utils.ApplicationURIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * Controller to handle Admin report downloads (Caller, Listener, Payment, etc.).
 * Each API generates an Excel file in memory and streams it directly to the client
 * as a downloadable attachment.
 */
@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN + ApplicationURIConstants.REPORT)
public class ApiV1AdminReportDownloadController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiV1AdminReportDownloadController.class);

    /**
     * Downloads the Caller report in Excel format.
     * The report data is fetched from the service layer, written to an Excel file in memory,
     * and returned as a downloadable attachment.
     *
     * @return ResponseEntity containing the Excel file bytes
     */
    @GetMapping(ApplicationURIConstants.CALLER)
    public ResponseEntity<byte[]> downloadCallerReport() {
        byte[] callerProfileList = null;
        try {
            LOGGER.info("Caller report download request received.");

            // Fetch data and generate report from service layer
            callerProfileList = getServiceRegistry()
                    .getCallerReportService()
                    .getReportOfAllCaller();

            // Create timestamped file name for download
            String fileName = getServiceRegistry().getExcelGenerator().generateTimestampedFileName("caller_report");
            LOGGER.debug("Caller report file name generated: {}", fileName);

            LOGGER.info("Caller report generated successfully. Sending file.");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(callerProfileList);

        } catch (Exception e) {
            LOGGER.error("Error while generating Caller report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(callerProfileList);
        }
    }

    /**
     * Downloads the Listener report in Excel format.
     * The report data is fetched from the service layer, written to an Excel file in memory,
     * and returned as a downloadable attachment.
     *
     * @return ResponseEntity containing the Excel file bytes
     * @throws IOException if file generation fails
     */
    @GetMapping(ApplicationURIConstants.LISTENER)
    public ResponseEntity<byte[]> downloadListenerReport() throws IOException {
        byte[] listenerProfileList = null;
        try {
            LOGGER.info("Listener report download request received.");

            // Fetch data and generate report from service layer
            listenerProfileList = getServiceRegistry()
                    .getListenerReportService()
                    .getReportOfAllListener();

            // Create timestamped file name for download
            String fileName = getServiceRegistry().getExcelGenerator().generateTimestampedFileName("listener_report");
            LOGGER.info("Listener report file name generated: {}", fileName);

            LOGGER.info("Listener report generated successfully. Sending file.");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(listenerProfileList);

        } catch (Exception e) {
            LOGGER.error("Error while generating Listener report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(listenerProfileList);
        }
    }

    /**
     * Downloads the Payment report in Excel format.
     * (Implementation pending: Fetch payment data, generate Excel, and return as attachment)
     *
     * @return ResponseEntity containing the Excel file bytes
     * @throws IOException if file generation fails
     */
    @GetMapping(ApplicationURIConstants.PAYMENT)
    public ResponseEntity<byte[]> downloadPaymentReport() throws IOException {

        byte[] paymentReport = null;
        try {
            LOGGER.info("payment report download request received.");

            // Fetch data and generate report from service layer
            paymentReport = getServiceRegistry()
                    .getPaymentReportService()
                    .getReportOfAllPayment();

            // Create timestamped file name for download
            String fileName = getServiceRegistry().getExcelGenerator().generateTimestampedFileName("payment_report");
            LOGGER.info("Payment report file name generated: {}", fileName);

            LOGGER.info("Payment report generated successfully. Sending file.");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(paymentReport);

        } catch (Exception e) {
            LOGGER.error("Error while generating Listener report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(paymentReport);
        }
    }

    @GetMapping(ApplicationURIConstants.DOWNLOAD_ZIP)
    public ResponseEntity<byte[]> downloadZipFile(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        LOGGER.info("Received request to download ZIP for listener profiles between {} and {}", startDate, endDate);

        List<ListenerProfile> listenerProfileList;
        try {
            listenerProfileList = getServiceRegistry()
                    .getListenerProfileService()
                    .findProfilesByCreatedAtBetweenAndMinEarning(startDate, endDate, 600.0);
        } catch (Exception e) {
            LOGGER.error("Failed to fetch listener profiles from database: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

        if (listenerProfileList == null || listenerProfileList.isEmpty()) {
            LOGGER.warn("No active listener profiles found between {} and {}", startDate, endDate);
            return ResponseEntity.noContent().build();
        }

        File tempZip;
        try {
            tempZip = File.createTempFile("listener_profiles-", ".zip");
        } catch (IOException e) {
            LOGGER.error("Failed to create temporary ZIP file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(tempZip.toPath()))) {

            for (ListenerProfile listenerProfile : listenerProfileList) {
                if (listenerProfile == null) {
                    LOGGER.warn("Skipped null listener profile while processing ZIP");
                    continue;
                }

                try {
                    getServiceRegistry()
                            .getDownloadZipFileService()
                            .processFileForZip(zos, listenerProfile.getIdProof(), "idProof", listenerProfile.getId());
                } catch (Exception e) {
                    LOGGER.error("Failed to process ID Proof for listenerProfile id={}: {}", listenerProfile.getId(), e.getMessage(), e);
                }

                try {
                    getServiceRegistry()
                            .getDownloadZipFileService()
                            .processFileForZip(zos, listenerProfile.getW9Form(), "w9Form", listenerProfile.getId());
                } catch (Exception e) {
                    LOGGER.error("Failed to process W9 Form for listenerProfile id={}: {}", listenerProfile.getId(), e.getMessage(), e);
                }
            }

        } catch (IOException e) {
            LOGGER.error("Error while writing ZIP file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        byte[] zipBytes;
        try (FileInputStream fis = new FileInputStream(tempZip);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            fis.transferTo(baos);
            zipBytes = baos.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Failed to read temporary ZIP file into byte array: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } finally {
            if (!tempZip.delete()) {
                LOGGER.warn("Temporary ZIP file could not be deleted: {}", tempZip.getAbsolutePath());
            }
        }

        LOGGER.info("ZIP file created successfully with {} listener profiles", listenerProfileList.size());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Documents.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipBytes);
    }

}
