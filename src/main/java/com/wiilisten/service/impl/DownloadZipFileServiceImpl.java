package com.wiilisten.service.impl;

import com.wiilisten.entity.CallerProfile;
import com.wiilisten.service.DownloadZipFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DownloadZipFileServiceImpl extends BaseServiceImpl<CallerProfile, Long> implements DownloadZipFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadZipFileServiceImpl.class);

    @Override
    public void processFileForZip(ZipOutputStream zos, String fileUrl, String filePrefix, Long userId) {
        if (fileUrl == null || fileUrl.isBlank()) {
            LOGGER.warn("Skipped {} because fileUrl is null or empty", filePrefix);
            return;
        }

        try {

            if (userId == null) {
                LOGGER.warn("Skipped {} because userId could not be extracted from URL: {}", filePrefix, fileUrl);
                return;
            }

            byte[] fileContent = downloadFileFromUrl(fileUrl);
            if (fileContent == null || fileContent.length == 0) {
                LOGGER.warn("Skipped {} because file content is empty for URL: {}", filePrefix, fileUrl);
                return;
            }

            String fileName = filePrefix + getFileExtension(fileUrl);
            zos.putNextEntry(new ZipEntry("Document/" + userId + "/" + fileName));
            zos.write(fileContent);
            zos.closeEntry();

            LOGGER.info("Added {} for userId={} from URL={}", filePrefix, userId, fileUrl);

        } catch (Exception e) {
            LOGGER.error("Error processing {} from URL {}: {}", filePrefix, fileUrl, e.getMessage(), e);
        }
    }

    @Override
    public byte[] downloadFileFromUrl(String fileUrl) {
        try (InputStream in = new URL(fileUrl).openStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            in.transferTo(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Error downloading file from URL {}: {}", fileUrl, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getFileExtension(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            int dotIndex = fileName.lastIndexOf(".");
            return (dotIndex != -1) ? fileName.substring(dotIndex) : "";
        } catch (Exception e) {
            LOGGER.error("Error extracting file extension from URL {}: {}", fileUrl, e.getMessage(), e);
            return "";
        }
    }
}
