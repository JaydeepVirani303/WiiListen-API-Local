package com.wiilisten.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiilisten.response.FileDownloadResponseDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;

@Component
public class ExcelGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelGenerator.class);

    /**
     * Generates an Excel file from a list of raw data maps.
     *
     * @param rawData List of maps where each map represents a row of data.
     *                Keys are column names, values are cell data.
     *                Nested maps are flattened automatically.
     * @return ByteArrayInputStream containing the generated Excel file.
     * @throws IOException if there is an error during Excel file generation.
     */
    private ByteArrayInputStream generate(List<Map<String, Object>> rawData) throws IOException {
        LOGGER.info("Starting Excel generation. Raw data size: {}", rawData.size());

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Create a sheet named "Report"
            Sheet sheet = workbook.createSheet("Report");

            // Set to collect all unique headers from all rows
            Set<String> headersSet = new LinkedHashSet<>();
            List<Map<String, Object>> flattenedData = new ArrayList<>();

            LOGGER.debug("Flattening each row and collecting headers...");
            for (Map<String, Object> record : rawData) {
                Map<String, Object> flat = flatten(record);
                flattenedData.add(flat);
                headersSet.addAll(flat.keySet());
            }

            // Convert headers set to list to maintain insertion order
            List<String> headers = new ArrayList<>(headersSet);
            LOGGER.debug("Collected {} headers: {}", headers.size(), headers);

            // ===== Write Headers =====
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }

            // ===== Write Data Rows =====
            LOGGER.debug("Writing data rows into Excel sheet...");
            int rowIdx = 1;
            for (Map<String, Object> rowMap : flattenedData) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < headers.size(); i++) {
                    Object value = rowMap.get(headers.get(i));
                    row.createCell(i).setCellValue(value != null ? value.toString() : "");
                }
            }

            // Auto-size all columns for better readability
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            LOGGER.info("Excel generation completed successfully. Total rows: {}", flattenedData.size());

            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    /**
     * Flattens a nested map into a single-level map using dot notation for nested keys.
     * Example:
     * <pre>
     * { "user": { "name": "John" }, "age": 30 }
     * → { "user.name": "John", "age": 30 }
     * </pre>
     *
     * @param map the map to flatten
     * @return flattened map
     */
    private static Map<String, Object> flatten(Map<String, Object> map) {
        Map<String, Object> flatMap = new LinkedHashMap<>();
        flattenRecursive("", map, flatMap);
        return flatMap;
    }

    /**
     * Recursively flattens a map by traversing nested maps and appending keys in dot notation.
     *
     * @param prefix  current key prefix (used for nested keys)
     * @param map     current map being processed
     * @param flatMap result map with flattened structure
     */
    @SuppressWarnings("unchecked")
    private static void flattenRecursive(String prefix, Map<String, Object> map, Map<String, Object> flatMap) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                flattenRecursive(key, (Map<String, Object>) value, flatMap);
            } else {
                flatMap.put(key, value);
            }
        }
    }

    /**
     * Generates an Excel report from the given list of DTOs and saves it to the Downloads folder.
     * Supports any DTO type by converting it to a map for dynamic column headers.
     *
     * @param dtoList        List of DTOs to export
     * @param reportBaseName Base name of the report file
     * @param <T>            DTO type
     * @return Response with file details and status
     * @throws IOException If file generation or saving fails
     */
    public <T> FileDownloadResponseDto generateExcelReport(List<T> dtoList, String reportBaseName) throws IOException {
        LOGGER.info("Starting generation of {} Excel file...", reportBaseName);

        if (dtoList == null || dtoList.isEmpty()) {
            LOGGER.warn("No data found for {} report generation.", reportBaseName);
            return FileDownloadResponseDto.builder()
                    .fileName(null)
                    .downloadTime(LocalDateTime.now().toString())
                    .message("No data found for " + reportBaseName)
                    .build();
        }

        // Convert DTO list to list of maps for dynamic Excel headers
        LOGGER.debug("Converting {} DTO records to map format for Excel generation...", dtoList.size());
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> dataMapList =
                mapper.convertValue(dtoList, new TypeReference<List<Map<String, Object>>>() {
                });
        LOGGER.debug("Converted {} records to map format.", dataMapList.size());

        // Generate Excel in memory
        LOGGER.info("Generating Excel file in memory...");
        ByteArrayInputStream in = generate(dataMapList);
        byte[] fileBytes = in.readAllBytes();
        LOGGER.info("Excel file generated successfully. Size: {} bytes", fileBytes.length);

        // Create timestamped file name
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = reportBaseName + "_" + timestamp + ".xlsx";
        LOGGER.debug("Generated report file name: {}", fileName);

        // Save file into Downloads folder
        Path downloadsPath = Paths.get(System.getProperty("user.home"), "Downloads", fileName);
        Files.write(downloadsPath, fileBytes);
        LOGGER.info("{} report saved to: {}", reportBaseName, downloadsPath.toAbsolutePath());

        // Build and return DTO
        LOGGER.info("{} report generation process completed.", reportBaseName);
        return FileDownloadResponseDto.builder()
                .fileName(fileName)
                .downloadTime(LocalDateTime.now().toString())
                .message(reportBaseName + " generated successfully.")
                .build();
    }
}


