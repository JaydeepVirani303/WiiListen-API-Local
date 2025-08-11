package com.wiilisten.service.impl;

import com.wiilisten.entity.CallerProfile;
import com.wiilisten.request.CombinedCallerUserDTO;
import com.wiilisten.response.FileDownloadResponseDto;
import com.wiilisten.service.CallerReportService;
import com.wiilisten.utils.ExcelGenerator;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Service implementation for generating caller reports in Excel format.
 * Fetches all caller profiles, maps them to DTOs, and creates an Excel file
 * with dynamic headers before saving it locally and returning file details.
 */
@Service
public class CallerReportServiceImpl extends BaseServiceImpl<CallerProfile, Long> implements CallerReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallerReportServiceImpl.class);

    private final ExcelGenerator excelGenerator;

    public CallerReportServiceImpl(ExcelGenerator excelGenerator) {
        this.excelGenerator = excelGenerator;
    }

    @PostConstruct
    public void setBaseRepository() {
        super.baseRepository = getDaoFactory().getCallerProfileRepository();
        LOGGER.info("Base repository for CallerReportServiceImpl initialized.");
    }

    @Override
    public List<CallerProfile> getAllCallers() {
        LOGGER.debug("Fetching all caller profiles from repository...");
        List<CallerProfile> callers = getDaoFactory().getCallerProfileRepository().findAll();
        LOGGER.info("Fetched {} caller profiles.", callers.size());
        return callers;
    }

    @Override
    public FileDownloadResponseDto getReportOfAllCaller() throws IOException {
        LOGGER.info("Starting generation of Caller Report Excel file...");
        // Step 1: Fetch caller profiles
        List<CallerProfile> callerProfileList = getAllCallers();

        // Step 2: Map to DTOs
        LOGGER.debug("Mapping {} caller profiles to CombinedCallerUserDTO...", callerProfileList.size());
        List<CombinedCallerUserDTO> dtoList = callerProfileList.stream()
                .map(CombinedCallerUserDTO::toDTO)
                .toList();

        return excelGenerator.generateExcelReport(dtoList, "caller_report");
    }
}
