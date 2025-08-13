package com.wiilisten.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Utility component responsible for generating Excel reports dynamically
 * from any DTO or raw map data.
 * <p>
 * Features:
 * - Dynamically collects headers from object fields.
 * - Supports nested objects by flattening them into dot notation keys.
 * - Produces Excel files entirely in memory (returns byte[] for download).
 */
@Component
public class ExcelGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelGenerator.class);

    /**
     * Core method for generating Excel file from flattened raw data maps.
     *
     * @param rawData list of maps where each map represents a row (column name → value)
     * @return ByteArrayInputStream of the generated Excel file
     * @throws IOException if file writing fails
     */
    private ByteArrayInputStream generate(List<Map<String, Object>> rawData) throws IOException {
        LOGGER.info("Starting Excel generation. Input record count: {}", rawData.size());

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Create a new sheet for the report
            Sheet sheet = workbook.createSheet("Report");

            // Step 1: Collect all unique headers from data
            Set<String> headersSet = new LinkedHashSet<>();
            List<Map<String, Object>> flattenedData = new ArrayList<>();

            LOGGER.debug("Flattening data and collecting unique headers...");
            for (Map<String, Object> record : rawData) {
                Map<String, Object> flat = flatten(record);
                flattenedData.add(flat);
                headersSet.addAll(flat.keySet());
            }

            List<String> headers = new ArrayList<>(headersSet);
            LOGGER.debug("Final header count: {} | Headers: {}", headers.size(), headers);

            // Step 2: Write headers to first row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }

            // Step 3: Write each data row
            LOGGER.debug("Writing {} data rows into Excel...", flattenedData.size());
            int rowIdx = 1;
            for (Map<String, Object> rowMap : flattenedData) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < headers.size(); i++) {
                    Object value = rowMap.get(headers.get(i));
                    row.createCell(i).setCellValue(value != null ? value.toString() : "");
                }
            }

            // Step 4: Auto-size columns for better readability
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Step 5: Write to output stream
            workbook.write(out);
            LOGGER.info("Excel generation completed. Total rows (excluding header): {}", flattenedData.size());

            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    /**
     * Flattens nested map structures into a single-level map using dot notation.
     * Example:
     * { "user": { "name": "John" }, "age": 30 }
     * → { "user.name": "John", "age": 30 }
     *
     * @param map nested map to flatten
     * @return flattened map
     */
    private static Map<String, Object> flatten(Map<String, Object> map) {
        Map<String, Object> flatMap = new LinkedHashMap<>();
        flattenRecursive("", map, flatMap);
        return flatMap;
    }

    /**
     * Recursive helper for flattening nested maps into dot notation keys.
     *
     * @param prefix  current key prefix
     * @param map     current map being processed
     * @param flatMap flattened map being built
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
     * Generates an Excel report from a list of DTOs.
     * <p>
     * Process:
     * 1. Convert DTOs to list of maps.
     * 2. Flatten nested fields for dynamic headers.
     * 3. Generate Excel in memory and return as byte[].
     * </p>
     *
     * @param dtoList list of DTO objects to export
     * @param <T>     DTO type
     * @return byte array representing the generated Excel file
     * @throws IOException if Excel generation fails
     */
    public <T> byte[] generateExcelReport(List<T> dtoList) throws IOException {
        LOGGER.info("Starting Excel report generation for {} records.",
                (dtoList != null ? dtoList.size() : 0));

        if (dtoList == null || dtoList.isEmpty()) {
            LOGGER.warn("No data provided for Excel generation.");
            throw new IOException("No data provided for Excel generation.");
        }

        // Step 1: Convert DTOs to map format
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> dataMapList = mapper.convertValue(
                dtoList, new TypeReference<List<Map<String, Object>>>() {
                }
        );
        LOGGER.debug("Converted DTOs to {} map records for Excel generation.", dataMapList.size());

        // Step 2: Generate Excel
        LOGGER.debug("Generating Excel file in memory...");
        ByteArrayInputStream in = generate(dataMapList);
        byte[] fileBytes = in.readAllBytes();

        LOGGER.info("Excel file generated successfully. File size: {} bytes.", fileBytes.length);
        return fileBytes;
    }
}
