package com.wiilisten.controller.api.admin;

import com.wiilisten.utils.ApplicationURIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN + ApplicationURIConstants.VIDEO)
public class ApiV1AdminVideoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiV1AdminVideoController.class);

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    @PostMapping(ApplicationURIConstants.COMPRESS)
    public ResponseEntity<byte[]> compressVideo(@RequestParam("file") MultipartFile file) {
        LOGGER.info("Received video compression request for file: {}", file.getOriginalFilename());

        String tempDir = System.getProperty("java.io.tmpdir");
        File inputFile = null;
        File outputFile = null;

        try {
            // Save uploaded file temporarily
            inputFile = new File(tempDir, Objects.requireNonNull(file.getOriginalFilename()));
            file.transferTo(inputFile);
            LOGGER.info("Saved uploaded file at temporary location: {}", inputFile.getAbsolutePath());

            // Define output temporary file
            outputFile = new File(tempDir, "compressed_" + file.getOriginalFilename());
            LOGGER.info("Output file will be saved at: {}", outputFile.getAbsolutePath());

            // FFmpeg command
            List<String> command = Arrays.asList(
                    ffmpegPath,
                    "-i", inputFile.getAbsolutePath(),
                    "-vcodec", "libx264",
                    "-crf", "28",
                    "-preset", "fast",
                    outputFile.getAbsolutePath()
            );
            LOGGER.info("Executing FFmpeg command: {}", String.join(" ", command));

            // Run process
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    LOGGER.info("FFmpeg log: {}", line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                LOGGER.info("FFmpeg process failed with exit code: {}", exitCode);
                return ResponseEntity.status(500)
                        .body(("FFmpeg failed with exit code " + exitCode).getBytes());
            }

            // Convert compressed file to byte[]
            byte[] videoBytes = Files.readAllBytes(outputFile.toPath());
            LOGGER.info("Video compression completed successfully. Original size: {} KB, Compressed size: {} KB",
                    inputFile.length() / 1024, outputFile.length() / 1024);

            // Clean up
            if (inputFile.delete()) {
                LOGGER.info("Deleted temporary input file: {}", inputFile.getAbsolutePath());
            } else {
                LOGGER.info("Failed to delete temporary input file: {}", inputFile.getAbsolutePath());
            }

            outputFile.deleteOnExit();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + outputFile.getName())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(videoBytes);

        } catch (Exception e) {
            LOGGER.info("Error occurred while compressing video: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(("Error compressing video: " + e.getMessage()).getBytes());
        }
    }
}
