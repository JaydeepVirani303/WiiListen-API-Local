package com.wiilisten.controller.api.admin;

import com.wiilisten.controller.BaseController;
import com.wiilisten.response.FileDownloadResponseDto;
import com.wiilisten.service.impl.CallerReportServiceImpl;
import com.wiilisten.utils.ApplicationURIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN + ApplicationURIConstants.REPORT)
public class ApiV1AdminReportDownloadController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallerReportServiceImpl.class);

    /**
     * Downloads the Caller report as an Excel file.
     * This method fetches caller-related data from the database,
     * generates an Excel file in memory, and streams it back in the HTTP response,
     * so it can be downloaded by the admin (browser or mobile app).
     *
     * @return ResponseEntity containing the Excel file as an attachment
     */
    @GetMapping(ApplicationURIConstants.CALLER)
    public ResponseEntity<FileDownloadResponseDto> downloadCallerReport() {
        FileDownloadResponseDto callerProfileList = new FileDownloadResponseDto();
        try {
            LOGGER.info("Caller report download request received.");

            callerProfileList =
                    getServiceRegistry().getCallerReportService().getReportOfAllCaller();

            LOGGER.info("Caller report generated successfully: {}", callerProfileList.getFileName());
            return ResponseEntity.ok(callerProfileList);

        } catch (Exception e) {
            LOGGER.error("Unexpected error while downloading caller report: {}", e.getMessage(), e);
            FileDownloadResponseDto errorResponse = FileDownloadResponseDto.builder()
                    .message("Unexpected error occurred: " + e.getMessage()).build();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    /**
     * Downloads the Listener report as an Excel file.
     * This method retrieves listener-related data from the database,
     * uses an Excel generator to create the file, and sends it as a downloadable
     * attachment in the HTTP response.
     *
     * @return ResponseEntity containing the Excel file as an attachment
     */
    @GetMapping(ApplicationURIConstants.LISTENER)
    public ResponseEntity<FileDownloadResponseDto> downloadListenerReport() throws IOException {
        FileDownloadResponseDto listenerProfileList = new FileDownloadResponseDto();
        try {
            LOGGER.info("Listener report download request received.");

            listenerProfileList =
                    getServiceRegistry().getListenerReportService().getReportOfAllListener();

            LOGGER.info("Listener report generated successfully: {}", listenerProfileList.getFileName());
            return ResponseEntity.ok(listenerProfileList);

        } catch (Exception e) {
            LOGGER.error("Unexpected error while downloading caller report: {}", e.getMessage(), e);
            FileDownloadResponseDto errorResponse = FileDownloadResponseDto.builder()
                    .message("Unexpected error occurred: " + e.getMessage()).build();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    /**
     * Downloads the Payment report as an Excel file.
     * This method queries payment-related data, builds an Excel sheet with that data,
     * and returns it in the HTTP response so that it can be downloaded by the client.
     *
     * @return ResponseEntity containing the Excel file as an attachment
     */
    @GetMapping(ApplicationURIConstants.PAYMENT)
    public ResponseEntity<?> downloadPaymentReport() throws IOException {
        return null;
    }

}
