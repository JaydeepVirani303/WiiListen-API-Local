package com.wiilisten.service.impl;

import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.PdfFile;
import com.wiilisten.repo.PdfRepository;
import com.wiilisten.service.DownloadZipFileService;
import com.wiilisten.utils.AESUtil;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DownloadZipFileServiceImpl extends BaseServiceImpl<CallerProfile, Long> implements DownloadZipFileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadZipFileServiceImpl.class);

    private final PdfRepository pdfRepository;

    public DownloadZipFileServiceImpl(PdfRepository pdfRepository) {
        this.pdfRepository = pdfRepository;
    }

    /**
     * Adds a file (W9 or ID Proof) into the ZIP stream.
     * If PDF is encrypted → decrypt using password from DB.
     * All PDFs in ZIP will be unprotected, and ZIP will be password protected (1234).
     */
    @Override
    public void addFileToZip(ZipOutputStream zos, String filePath, String fileType,
                             Long userId, String userName) {

        if (filePath == null || filePath.isEmpty()) {
            LOGGER.warn("Empty file path for user {}", userId);
            return;
        }

        InputStream inputStream = null;
        File tempPdfFile = null;
        File decryptedPdf = null;

        try {
            // 1. Download file from URL or local path
            if (filePath.startsWith("http")) {
                URL url = new URL(filePath);
                tempPdfFile = File.createTempFile("download_", ".pdf");
                try (InputStream in = url.openStream();
                     OutputStream out = new FileOutputStream(tempPdfFile)) {
                    in.transferTo(out);
                }
            } else {
                tempPdfFile = new File(filePath);
                if (!tempPdfFile.exists()) {
                    LOGGER.warn("Local file not found for user {}: {}", userId, filePath);
                    return;
                }
            }

            String suffix = fileType.equalsIgnoreCase("w9Form") ? "w9s" : "id";
            String ext = getFileExtension(filePath);
            if (ext == null) ext = "pdf";

            String entryName = "w9s_report/" + userId + "_" + userName + "_" + suffix + "." + ext;

            File finalPdf = tempPdfFile;

            // 2. Check if PDF is encrypted
            if (ext.equalsIgnoreCase("pdf") && isPdfEncrypted(tempPdfFile)) {
                LOGGER.info("PDF is encrypted, fetching password from DB for user {}", userId);

                // Fetch password from DB
                Optional<PdfFile> pdfFileOpt = pdfRepository.findByFileUrl(filePath);
                String encryptedOldPassword = pdfFileOpt.map(PdfFile::getPassword).orElse(null);
                String oldPassword = encryptedOldPassword != null ? AESUtil.decrypt(encryptedOldPassword) : null;

                if (oldPassword != null) {
                    // 3. Decrypt PDF
                    decryptedPdf = decryptPdf(tempPdfFile, oldPassword);
                    finalPdf = decryptedPdf;
                    LOGGER.info("Decrypted PDF for user {}", userId);
                } else {
                    LOGGER.warn("Password not found in DB for user {} and file {}", userId, filePath);
                }
            }

            // 4. Add decrypted/unprotected file to ZIP
            LOGGER.info("Adding to ZIP: {}", entryName);
            try (InputStream fileStream = new FileInputStream(finalPdf)) {
                zos.putNextEntry(new ZipEntry(entryName));
                fileStream.transferTo(zos);
                zos.closeEntry();
            }

        } catch (Exception e) {
            LOGGER.error("Failed to add file to ZIP for user {}: {}", userId, e.getMessage(), e);
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException ignored) {
            }
            if (tempPdfFile != null && tempPdfFile.exists() && filePath.startsWith("http")) tempPdfFile.delete();
            if (decryptedPdf != null && decryptedPdf.exists()) decryptedPdf.delete();
        }
    }

    /**
     * Check if a PDF is password protected.
     */
    private boolean isPdfEncrypted(File pdfFile) {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            return false;
        } catch (IOException e) {
            // PDFBox throws IOException if it's password protected
            return e.getMessage() != null && e.getMessage().toLowerCase().contains("password");
        }
    }

    /**
     * Decrypt an encrypted PDF using the given password.
     */
    private File decryptPdf(File encryptedPdf, String password) throws IOException {
        File tempFile = File.createTempFile("decrypted_", ".pdf");
        try (PDDocument document = PDDocument.load(encryptedPdf, password)) {
            document.setAllSecurityToBeRemoved(true);
            document.save(tempFile);
        }
        return tempFile;
    }

    /**
     * Encrypt a PDF with a given password (used for ZIP-level encryption later).
     */
    private File encryptPdf(InputStream pdfStream, String password) throws IOException {
        PDDocument document = PDDocument.load(pdfStream);
        AccessPermission ap = new AccessPermission();
        StandardProtectionPolicy spp = new StandardProtectionPolicy(password, password, ap);
        spp.setEncryptionKeyLength(128);
        spp.setPermissions(ap);

        document.protect(spp);
        File tempEncrypted = File.createTempFile("encrypted_", ".pdf");
        document.save(tempEncrypted);
        document.close();

        return tempEncrypted;
    }

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

    public byte[] applyPasswordToZipBytes(byte[] zipBytes, String password) throws IOException {
        // 1. Write original ZIP bytes to a temporary file
        File tempZip = File.createTempFile("tempZip_", ".zip");
        try (FileOutputStream fos = new FileOutputStream(tempZip)) {
            fos.write(zipBytes);
        }

        // 2. Create password-protected ZIP file
        File protectedZip = File.createTempFile("protectedZip_", ".zip");
        ZipFile zipFile = new ZipFile(protectedZip, password.toCharArray());
        ZipParameters params = new ZipParameters();
        params.setCompressionMethod(CompressionMethod.DEFLATE);
        params.setEncryptFiles(true);
        params.setEncryptionMethod(EncryptionMethod.AES);

        // 3. Extract original ZIP and add files with original entry names
        try (java.util.zip.ZipFile oldZip = new java.util.zip.ZipFile(tempZip)) {
            oldZip.stream().forEach(entry -> {
                if (!entry.isDirectory()) {
                    try (InputStream is = oldZip.getInputStream(entry)) {
                        // Zip4j allows adding input stream with a custom entry name
                        params.setFileNameInZip(entry.getName()); // preserve original name
                        zipFile.addStream(is, params);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        // 4. Read back password-protected ZIP bytes
        byte[] protectedBytes;
        try (FileInputStream fis = new FileInputStream(protectedZip);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            fis.transferTo(baos);
            protectedBytes = baos.toByteArray();
        }

        // 5. Clean up temp files
        tempZip.delete();
        protectedZip.delete();

        return protectedBytes;
    }


}
