package com.wiilisten.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.itextpdf.kernel.pdf.*;
import com.wiilisten.config.S3BucketProperties;
import com.wiilisten.entity.PdfFile;
import com.wiilisten.entity.SystemSetting;
import com.wiilisten.entity.PdfPasswordChangeHistory;
import com.wiilisten.entity.User;
import com.wiilisten.repo.PdfRepository;
import com.wiilisten.repo.SystemSettingRepository;
import com.wiilisten.repo.PdfPasswordChangeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class PdfEncryptionService {

    private final PdfRepository pdfRepository;
    private final AmazonS3 amazonS3;
    private final S3BucketProperties s3BucketProperties;
    private final SystemSettingRepository systemSettingRepository;
    private final PdfPasswordChangeHistoryRepository pdfPasswordChangeHistoryRepository;

    /**
     * Apply random password record and overwrite in S3 (without breaking public access)
     * If PDF already has a password, decrypt first then apply new password
     */
    public void applyPasswordAndOverwrite(String publicUrl) throws IOException {
        String bucketName = s3BucketProperties.getBucket();
        String key = extractS3KeyFromUrl(publicUrl);

        // 1. Check if PDF already has a password in DB
        Optional<PdfFile> existingPdfOpt = pdfRepository.findByFileUrl(publicUrl);
        String encryptedOldPassword = existingPdfOpt.map(PdfFile::getPassword).orElse(null);
        String oldPassword = encryptedOldPassword != null ? AESUtil.decrypt(encryptedOldPassword) : null;

        // 2. Download PDF
        File originalPdf = downloadFileFromS3(publicUrl);

        // 3. Decrypt if old password exists
        File pdfToUpload = oldPassword != null ? decryptPdf(originalPdf, oldPassword) : originalPdf;

        // 4. Get active global password
        String newPassword = getGlobalPdfPassword();
        // 5. Encrypt PDF with new password
        File encryptedPdf = encryptPdf(pdfToUpload, newPassword);

        String encryptedNewPassword = AESUtil.encrypt(newPassword);

        // 6. Upload encrypted PDF back to S3 (public)
        amazonS3.putObject(new PutObjectRequest(bucketName, key, encryptedPdf)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        // 7. Save/update password in DB
        PdfFile pdfFile = existingPdfOpt.orElseGet(() -> PdfFile.builder()
                .fileName(encryptedPdf.getName())
                .fileUrl(publicUrl)
                .build());
        pdfFile.setPassword(encryptedNewPassword);
        pdfRepository.save(pdfFile);

        // 8. Cleanup
        originalPdf.delete();
        if (pdfToUpload != originalPdf) pdfToUpload.delete();
        encryptedPdf.delete();
    }

    /**
     * Download PDF from S3
     */
    private File downloadFileFromS3(String publicUrl) throws IOException {
        String bucketName = s3BucketProperties.getBucket();
        String key = extractS3KeyFromUrl(publicUrl);
        File tempFile = File.createTempFile("aws-", ".pdf");

        try (InputStream in = amazonS3.getObject(bucketName, key).getObjectContent();
             OutputStream out = new FileOutputStream(tempFile)) {
            in.transferTo(out);
        }
        return tempFile;
    }

    /**
     * Encrypt PDF with password (UTF-8)
     */
    private File encryptPdf(File inputPdf, String password) throws IOException {
        File outputFile = File.createTempFile("encrypted_", ".pdf");

        try (PdfReader reader = new PdfReader(inputPdf.getAbsolutePath());
             PdfWriter writer = new PdfWriter(outputFile.getAbsolutePath(),
                     new WriterProperties().setStandardEncryption(
                             password.getBytes(StandardCharsets.UTF_8),
                             password.getBytes(StandardCharsets.UTF_8),
                             EncryptionConstants.ALLOW_PRINTING,
                             EncryptionConstants.ENCRYPTION_AES_256
                     ));
             PdfDocument pdfDoc = new PdfDocument(reader, writer)) {
        }

        return outputFile;
    }

    /**
     * Decrypt PDF with password (UTF-8)
     */
    private File decryptPdf(File encryptedPdf, String password) throws IOException {
        File outputFile = File.createTempFile("decrypted_", ".pdf");

        try (PdfReader reader = new PdfReader(encryptedPdf.getAbsolutePath(),
                new ReaderProperties().setPassword(password.getBytes(StandardCharsets.UTF_8)));
             PdfWriter writer = new PdfWriter(outputFile.getAbsolutePath());
             PdfDocument pdfDoc = new PdfDocument(reader, writer)) {
        }

        return outputFile;
    }

    /**
     * Generate secure random password
     */
    public static String generateRandomPassword(int length) {
        String LOWER = "abcdefghijklmnopqrstuvwxyz";
        String UPPER = LOWER.toUpperCase();
        String DIGITS = "0123456789";
        String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?";
        String ALL = LOWER + UPPER + DIGITS + SPECIAL;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        // Ensure at least one of each type
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        for (int i = 4; i < length; i++) {
            password.append(ALL.charAt(random.nextInt(ALL.length())));
        }

        // Shuffle characters
        char[] chars = password.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char tmp = chars[i];
            chars[i] = chars[j];
            chars[j] = tmp;
        }

        return new String(chars);
    }

    /**
     * Extract S3 key from public URL
     */
    private String extractS3KeyFromUrl(String publicUrl) {
        URI uri = URI.create(publicUrl);
        return uri.getPath().substring(1);
    }

    /**
     * ---------------------- Download API ----------------------
     */

    public ResponseEntity<byte[]> downloadPdfWithPassword(String fileUrl) throws IOException {
        /*
        // 1. Download PDF from S3
        File downloadedPdf = downloadFileFromS3(fileUrl);

        // 2. Check if PDF already has password in DB
        Optional<PdfFile> existingPdfOpt = pdfRepository.findByFileUrl(fileUrl);
        String oldPassword = existingPdfOpt.map(PdfFile::getPassword).orElse(null);

        File pdfToReturn = downloadedPdf;

        // 3. Decrypt PDF if password exists
        if (oldPassword != null) {
            oldPassword = AESUtil.decrypt(oldPassword);
            pdfToReturn = decryptPdf(downloadedPdf, oldPassword);
        }

        // 4. Read bytes to return
        byte[] pdfBytes;
        try (FileInputStream fis = new FileInputStream(pdfToReturn)) {
            pdfBytes = fis.readAllBytes();
        }

        // 5. Cleanup temporary files
        downloadedPdf.delete();
        if (pdfToReturn != downloadedPdf) pdfToReturn.delete();

        // 6. Return PDF as attachment
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=decrypted.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
        */

        // New Logic: Return the file as-is from S3 (which is password-protected)
        File downloadedPdf = downloadFileFromS3(fileUrl);

        byte[] pdfBytes;
        try (FileInputStream fis = new FileInputStream(downloadedPdf)) {
            pdfBytes = fis.readAllBytes();
        }

        downloadedPdf.delete();

        String key = extractS3KeyFromUrl(fileUrl);
        String filename = key.contains("/") ? key.substring(key.lastIndexOf("/") + 1) : "encrypted.pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    public String getDecryptedPassword(String publicUrl) {
        if (publicUrl == null || publicUrl.isEmpty()) return null;
        Optional<PdfFile> existingPdfOpt = pdfRepository.findByFileUrl(publicUrl);
        String encryptedOldPassword = existingPdfOpt.map(PdfFile::getPassword).orElse(null);
        return encryptedOldPassword != null ? AESUtil.decrypt(encryptedOldPassword) : null;
    }

    public java.util.Map<String, String> getDecryptedPasswordsBatch(java.util.Collection<String> publicUrls) {
        if (publicUrls == null || publicUrls.isEmpty()) return java.util.Collections.emptyMap();
        java.util.List<PdfFile> pdfFiles = pdfRepository.findByFileUrlIn(publicUrls);
        java.util.Map<String, String> resultMap = new java.util.HashMap<>();
        for (PdfFile pdfFile : pdfFiles) {
            if (pdfFile.getPassword() != null) {
                resultMap.put(pdfFile.getFileUrl(), AESUtil.decrypt(pdfFile.getPassword()));
            }
        }
        return resultMap;
    }

    public String getGlobalPdfPassword() {
        return systemSettingRepository.findBySettingKey("GLOBAL_PDF_PASSWORD")
                .map(setting -> AESUtil.decrypt(setting.getSettingValue()))
                .orElseThrow(() -> new RuntimeException("Global PDF password setting 'GLOBAL_PDF_PASSWORD' is not configured in the database."));
    }

    public void updateGlobalPdfPassword(User adminUser, String newPasswordRaw) {
        String oldPasswordRaw = getGlobalPdfPassword();

        String encryptedOldPassword = AESUtil.encrypt(oldPasswordRaw);
        String encryptedNewPassword = AESUtil.encrypt(newPasswordRaw);

        PdfPasswordChangeHistory history = PdfPasswordChangeHistory.builder()
                .changedByUser(adminUser)
                .oldPassword(encryptedOldPassword)
                .newPassword(encryptedNewPassword)
                .status(ApplicationConstants.PENDING)
                .details("Initiating batch re-encryption process...")
                .build();
        history = pdfPasswordChangeHistoryRepository.save(history);

        SystemSetting systemSetting = systemSettingRepository.findBySettingKey("GLOBAL_PDF_PASSWORD")
                .orElseThrow(() -> new RuntimeException("Global PDF password setting 'GLOBAL_PDF_PASSWORD' is not configured in the database."));
        systemSetting.setSettingValue(encryptedNewPassword);
        systemSettingRepository.save(systemSetting);

        final Long historyId = history.getId();
        CompletableFuture.runAsync(() -> {
            processReencryption(historyId, oldPasswordRaw, newPasswordRaw);
        });
    }

    private void processReencryption(Long historyId, String oldPassword, String newPassword) {
        Optional<PdfPasswordChangeHistory> historyOpt = pdfPasswordChangeHistoryRepository.findById(historyId);
        if (historyOpt.isEmpty()) return;
        PdfPasswordChangeHistory history = historyOpt.get();

        history.setStatus(ApplicationConstants.IN_PROGRESS);
        history.setDetails("Re-encryption in progress. Fetching PDF files...");
        pdfPasswordChangeHistoryRepository.save(history);

        List<PdfFile> pdfFiles = pdfRepository.findAll();
        if (pdfFiles.isEmpty()) {
            history.setStatus(ApplicationConstants.COMPLETED);
            history.setDetails("No PDF files found to re-encrypt.");
            pdfPasswordChangeHistoryRepository.save(history);
            return;
        }

        int totalFiles = pdfFiles.size();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        ConcurrentLinkedQueue<String> errorLogs = new ConcurrentLinkedQueue<>();

        ExecutorService executor = Executors.newFixedThreadPool(Math.min(10, totalFiles));

        for (PdfFile pdfFile : pdfFiles) {
            executor.submit(() -> {
                try {
                    String publicUrl = pdfFile.getFileUrl();
                    String bucketName = s3BucketProperties.getBucket();
                    String key = extractS3KeyFromUrl(publicUrl);

                    File originalPdf = downloadFileFromS3(publicUrl);

                    String storedEncryptedPassword = pdfFile.getPassword();
                    String fileOldPassword = (storedEncryptedPassword != null) ? AESUtil.decrypt(storedEncryptedPassword) : oldPassword;

                    File pdfToUpload = fileOldPassword != null ? decryptPdf(originalPdf, fileOldPassword) : originalPdf;

                    File encryptedPdf = encryptPdf(pdfToUpload, newPassword);

                    amazonS3.putObject(new PutObjectRequest(bucketName, key, encryptedPdf)
                            .withCannedAcl(CannedAccessControlList.PublicRead));

                    pdfFile.setPassword(AESUtil.encrypt(newPassword));
                    pdfRepository.save(pdfFile);

                    originalPdf.delete();
                    if (pdfToUpload != originalPdf) pdfToUpload.delete();
                    encryptedPdf.delete();

                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    errorLogs.add("Failed to re-encrypt file ID " + pdfFile.getId() + " (" + pdfFile.getFileName() + "): " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.MINUTES)) {
                executor.shutdownNow();
                errorLogs.add("Execution timed out before all tasks completed.");
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            errorLogs.add("Execution was interrupted.");
        }

        String finalDetails = String.format("Processed %d files. Success: %d, Failure: %d.\n", totalFiles, successCount.get(), failureCount.get());
        if (!errorLogs.isEmpty()) {
            finalDetails += "Errors:\n" + String.join("\n", errorLogs);
        }

        history.setDetails(finalDetails);
        if (failureCount.get() > 0) {
            history.setStatus("COMPLETED_WITH_ERRORS");
        } else {
            history.setStatus(ApplicationConstants.COMPLETED);
        }
        pdfPasswordChangeHistoryRepository.save(history);
    }

    public List<PdfPasswordChangeHistory> getPasswordChangeHistory() {
        return pdfPasswordChangeHistoryRepository.findAllByOrderByChangedAtDesc();
    }
}
