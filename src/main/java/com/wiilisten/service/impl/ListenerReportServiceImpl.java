package com.wiilisten.service.impl;

import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.request.CombinedListenerUserDTO;
import com.wiilisten.service.ListenerReportService;
import com.wiilisten.utils.ExcelGenerator;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Service implementation for generating Listener reports in Excel format.
 * <p>
 * Responsibilities:
 * 1. Fetch all listener profiles from the database.
 * 2. Map entity data to DTOs for cleaner Excel representation.
 * 3. Delegate Excel file generation to {@link ExcelGenerator}.
 * 4. Return the generated file as a byte array for API download.
 * </p>
 */
@Service
public class ListenerReportServiceImpl extends BaseServiceImpl<ListenerProfile, Long> implements ListenerReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerReportServiceImpl.class);

    private final ExcelGenerator excelGenerator;

    public ListenerReportServiceImpl(ExcelGenerator excelGenerator) {
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

    /**
     * Retrieves all listener profiles from the repository.
     *
     * @return list of {@link ListenerProfile} entities
     */
    @Override
    public List<ListenerProfile> getAllListeners() {
        LOGGER.info("Fetching all listener profiles from the repository...");
        List<ListenerProfile> listeners = getDaoFactory()
                .getListenerProfileRepository()
                .findAll();
        LOGGER.info("Fetched {} listener profiles from the database.", listeners.size());
        return listeners;
    }

    /**
     * Generates the Listener Report in Excel format.
     * <p>
     * Steps:
     * 1. Fetch all listener profiles from the database.
     * 2. Map them to {@link CombinedListenerUserDTO}.
     * 3. Generate an Excel file using the {@link ExcelGenerator}.
     * 4. Return the generated file as a byte array.
     * </p>
     *
     * @return byte array representing the Excel file
     * @throws IOException if Excel generation fails
     */
    @Override
    public byte[] getReportOfAllListener() throws IOException {
        LOGGER.info("Starting generation of Listener Report Excel file...");

        // Step 1: Fetch listener profiles
        List<ListenerProfile> listenerProfileList = getAllListeners();

        // Step 2: Map to DTOs
        LOGGER.info("Mapping {} listener profiles to CombinedListenerUserDTO objects...", listenerProfileList.size());
        List<CombinedListenerUserDTO> dtoList = listenerProfileList.stream()
                .map(CombinedListenerUserDTO::toDTO)
                .toList();
        LOGGER.info("Mapped {} listener profiles to DTOs successfully.", dtoList.size());

        // Step 3: Generate Excel file
        LOGGER.info("Generating Excel file for {} listener records...", dtoList.size());
        byte[] excelBytes = excelGenerator.generateExcelReport(dtoList);
        LOGGER.info("Listener Report Excel file generated successfully. File size: {} bytes.", excelBytes.length);

        return excelBytes;
    }
}
