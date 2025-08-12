package com.wiilisten.service.impl;

import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.request.CombinedCallerUserDTO;
import com.wiilisten.request.CombinedListenerUserDTO;
import com.wiilisten.response.FileDownloadResponseDto;
import com.wiilisten.service.ListenerReportService;
import com.wiilisten.utils.ExcelGenerator;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Service implementation for generating listener reports in Excel format.
 * Fetches all caller profiles, maps them to DTOs, and creates an Excel file
 * with dynamic headers before saving it locally and returning file details.
 */
@Service
public class ListenerReportServiceImpl extends BaseServiceImpl<ListenerProfile, Long> implements ListenerReportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerReportServiceImpl.class);
    private final ExcelGenerator excelGenerator;

    public ListenerReportServiceImpl(ExcelGenerator excelGenerator) {
        this.excelGenerator = excelGenerator;
    }

    @PostConstruct
    public void setBaseRepository() {
        super.baseRepository = getDaoFactory().getListenerProfileRepository();
        LOGGER.info("Base repository for CallerReportServiceImpl initialized.");
    }

    @Override
    public List<ListenerProfile> getAllListeners() {
        LOGGER.debug("Fetching all listener profiles from repository...");
        List<ListenerProfile> listeners = getDaoFactory().getListenerProfileRepository().findAll();
        LOGGER.info("Fetched {} listener profiles.", listeners.size());
        return listeners;
    }

    @Override
    public FileDownloadResponseDto getReportOfAllListener() throws IOException {
        LOGGER.info("Starting generation of Listener Report Excel file...");
        // Step 1: Fetch listener profiles
        List<ListenerProfile> listenerProfileList = getAllListeners();

        // Step 2: Map to DTOs
        LOGGER.debug("Mapping {} listener profiles to CombinedListenerUserDTO...", listenerProfileList.size());
        List<CombinedListenerUserDTO> dtoList = listenerProfileList.stream()
                .map(CombinedListenerUserDTO::toDTO)
                .toList();

        return excelGenerator.generateExcelReport(dtoList, "listener_report");
    }
}
