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

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.geom.PageSize;
import com.wiilisten.utils.PdfEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;

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

    @Autowired
    private PdfEncryptionService pdfEncryptionService;

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

    @Override
    public byte[] getListenerDocumentsReport() throws IOException {
        LOGGER.info("Starting generation of Listener Documents Report PDF file...");
        List<ListenerProfile> listenerProfileList = getAllListeners();

        // Filter profiles: only include if idProof is not null or empty
        List<ListenerProfile> filteredProfiles = listenerProfileList.stream()
                .filter(p -> p != null && p.getUser() != null)
                .filter(p -> p.getIdProof() != null && !p.getIdProof().trim().isEmpty())
                .toList();

        // Collect all URLs for batch password fetch
        java.util.Set<String> allUrls = new java.util.HashSet<>();
        for (ListenerProfile profile : filteredProfiles) {
            if (profile.getIdProof() != null && !profile.getIdProof().trim().isEmpty()) {
                allUrls.add(profile.getIdProof());
            }
            if (profile.getW9Form() != null && !profile.getW9Form().trim().isEmpty()) {
                allUrls.add(profile.getW9Form());
            }
        }

        // Batch fetch passwords
        java.util.Map<String, String> passwordMap = pdfEncryptionService.getDecryptedPasswordsBatch(allUrls);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            // Use A3 landscape for more width to prevent cutting
            pdfDoc.setDefaultPageSize(PageSize.A3.rotate());
            Document document = new Document(pdfDoc);
            document.setMargins(20, 20, 20, 20);

            // Title
            document.add(new Paragraph("Listener Documents & Passwords Report")
                    .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));

            // Create a table with 6 columns (Name column removed)
            Table table = new Table(new float[]{1, 3, 4, 2, 4, 2});
            table.setWidth(UnitValue.createPercentValue(100));

            // Headers
            table.addHeaderCell(new Cell().add(new Paragraph("ID").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Email").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("ID Proof").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("ID Pwd").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("W9 Form").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("W9 Pwd").setBold()));

            for (ListenerProfile profile : filteredProfiles) {
                String id = String.valueOf(profile.getId());
                String email = profile.getUser() != null && profile.getUser().getEmail() != null ? profile.getUser().getEmail() : "";

                String originalIdProof = profile.getIdProof() != null ? profile.getIdProof() : "";
                String originalW9Form = profile.getW9Form() != null ? profile.getW9Form() : "";

                // Insert Zero Width Space after common URL delimiters to allow wrapping and prevent cutting
                String idProofUrl = originalIdProof.replaceAll("([/?&=.-])", "$1\u200B");
                String w9FormUrl = originalW9Form.replaceAll("([/?&=.-])", "$1\u200B");

                String idProofPwd = passwordMap.getOrDefault(originalIdProof, "");
                String w9FormPwd = passwordMap.getOrDefault(originalW9Form, "");

                table.addCell(new Cell().add(new Paragraph(id).setFontSize(10)));
                table.addCell(new Cell().add(new Paragraph(email).setFontSize(10)));
                table.addCell(new Cell().add(new Paragraph(idProofUrl).setFontSize(9)));
                table.addCell(new Cell().add(new Paragraph(idProofPwd).setFontSize(10)));
                table.addCell(new Cell().add(new Paragraph(w9FormUrl).setFontSize(9)));
                table.addCell(new Cell().add(new Paragraph(w9FormPwd).setFontSize(10)));
            }

            document.add(table);
            document.close();

            LOGGER.info("Listener Documents Report PDF generated successfully.");
            return baos.toByteArray();
        }
    }
}
