package com.wiilisten.service.impl;

import com.wiilisten.entity.CallerProfile;
import com.wiilisten.service.DownloadZipFileService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
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
        File tempEncryptedPdf = null;

        try {
            if (filePath.startsWith("http")) {
                URL url = new URL(filePath);
                inputStream = url.openStream();
            } else {
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

            String entryName = "w9s_report/" + userId + "_" + firstName + "_" + lastName + "_" + suffix + "." + ext;

            InputStream finalInputStream = inputStream;

            // If PDF → encrypt before adding to ZIP
            if (ext.equalsIgnoreCase("pdf")) {
                tempEncryptedPdf = encryptPdf(finalInputStream, "1234");
                finalInputStream = new FileInputStream(tempEncryptedPdf);
            }

            LOGGER.info("Adding to ZIP: {}", entryName);
            zos.putNextEntry(new ZipEntry(entryName));
            finalInputStream.transferTo(zos);
            zos.closeEntry();

            finalInputStream.close();

        } catch (Exception e) {
            LOGGER.error("Failed to add file to ZIP for user {}: {}", userId, e.getMessage(), e);
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException ignore) {
            }

            if (tempEncryptedPdf != null && tempEncryptedPdf.exists()) {
                tempEncryptedPdf.delete();
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

    private File encryptPdf(InputStream pdfStream, String password) throws IOException {
        PDDocument document = PDDocument.load(pdfStream);

        AccessPermission ap = new AccessPermission();
        StandardProtectionPolicy spp = new StandardProtectionPolicy(password, password, ap);
        spp.setEncryptionKeyLength(128); // or 256
        spp.setPermissions(ap);

        document.protect(spp);

        File tempEncrypted = File.createTempFile("encrypted_", ".pdf");
        document.save(tempEncrypted);
        document.close();

        return tempEncrypted;
    }

}
