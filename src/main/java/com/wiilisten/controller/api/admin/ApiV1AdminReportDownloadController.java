package com.wiilisten.controller.api.admin;

import com.wiilisten.controller.BaseController;
import com.wiilisten.utils.ApplicationURIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
            String fileName = generateTimestampedFileName("caller_report");
            LOGGER.debug("Caller report file name generated: {}", fileName);

            LOGGER.info("Caller report generated successfully. Sending file to client.");
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
            String fileName = generateTimestampedFileName("listener_report");
            LOGGER.info("Listener report file name generated: {}", fileName);

            LOGGER.info("Listener report generated successfully. Sending file to client.");
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
    public ResponseEntity<?> downloadPaymentReport() throws IOException {
        LOGGER.info("Payment report download API called but not yet implemented.");
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("Payment report generation is not implemented yet.");
    }

    /**
     * Utility method to generate a timestamped file name.
     *
     * @param baseName base name of the file without extension
     * @return timestamped file name with .xlsx extension
     */
    private String generateTimestampedFileName(String baseName) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return baseName + "_" + timestamp + ".xlsx";
    }
}
