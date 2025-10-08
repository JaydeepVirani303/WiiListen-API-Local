package com.wiilisten.service.impl;

import com.wiilisten.entity.CallerProfile;
import com.wiilisten.service.DownloadZipFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DownloadZipFileServiceImpl extends BaseServiceImpl<CallerProfile, Long> implements DownloadZipFileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadZipFileServiceImpl.class);

    /**
     * Adds a file (W9 or ID Proof) into the ZIP stream with proper structure.
     */
    @Override
    public void addFileToZip(ZipOutputStream zos, String filePath, String fileType,
                             Long userId, String firstName, String lastName) {

        if (filePath == null || filePath.isEmpty()) {
            LOGGER.warn("Empty file path for user {}", userId);
            return;
        }

        InputStream inputStream = null;

        try {
            if (filePath.startsWith("http")) {
                // ✅ Handle remote (S3) file
                URL url = new URL(filePath);
                inputStream = url.openStream();
            } else {
                // ✅ Handle local file
                File file = new File(filePath);
                if (!file.exists()) {
                    LOGGER.warn("Local file not found for user {}: {}", userId, filePath);
                    return;
                }
                inputStream = new FileInputStream(file);
            }

            String suffix = fileType.equalsIgnoreCase("w9Form") ? "w9s" : "id";
            String ext = getFileExtension(filePath);
            if (ext == null) ext = "pdf";

            // ✅ Folder + filename format: w9s_report/{userId}_{fname}_{lname}_{suffix}.{ext}
            String entryName = "w9s_report/" + userId + "_" + firstName + "_" + lastName + "_" + suffix + "." + ext;

            LOGGER.info("Adding to ZIP: {}", entryName);
            zos.putNextEntry(new ZipEntry(entryName));

            inputStream.transferTo(zos);
            zos.closeEntry();

        } catch (Exception e) {
            LOGGER.error("Failed to add file to ZIP for user {}: {}", userId, e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    /**
     * Returns safe name (lowercase, no spaces)
     */
    @Override
    public String sanitizeName(String name) {
        if (name == null) return "";
        return name.trim().toLowerCase().replaceAll("\\s+", "_");
    }

    @Override
    public String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return (lastDot == -1) ? null : fileName.substring(lastDot + 1).toLowerCase();
    }

}
