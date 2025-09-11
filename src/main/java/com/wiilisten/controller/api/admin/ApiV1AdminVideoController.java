package com.wiilisten.controller.api.admin;

import com.wiilisten.utils.ApplicationURIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN + ApplicationURIConstants.VIDEO)
public class ApiV1AdminVideoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiV1AdminVideoController.class);

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    @PostMapping(ApplicationURIConstants.COMPRESS)
    public ResponseEntity<InputStreamResource> compressVideo(@RequestParam("file") MultipartFile file) {
        LOGGER.info("Received video compression request for file: {}", file.getOriginalFilename());

        long startTime = System.currentTimeMillis();

        String tempDir = System.getProperty("java.io.tmpdir");
        File inputFile = null;
        File outputFile = null;

        try {
            // Generate unique file names to avoid conflicts
            String uniqueId = UUID.randomUUID().toString();
            inputFile = new File(tempDir, uniqueId + "_" + Objects.requireNonNull(file.getOriginalFilename()));
            file.transferTo(inputFile);
            LOGGER.info("Saved uploaded file at temporary location: {}", inputFile.getAbsolutePath());

            outputFile = new File(tempDir, "compressed_" + uniqueId + "_" + file.getOriginalFilename());
            LOGGER.info("Output file will be saved at: {}", outputFile.getAbsolutePath());

            // Optimized FFmpeg command
            List<String> command = Arrays.asList(
                    ffmpegPath,
                    "-i", inputFile.getAbsolutePath(),
                    "-vcodec", "libx264",
                    "-crf", "35",                  // adjust CRF for quality/speed
                    "-preset", "ultrafast",        // ⚡ much faster than "fast"
                    "-tune", "zerolatency",        // low-latency optimization
                    "-threads", "4",               // use multiple CPU cores
                    "-movflags", "+faststart",     // good for streaming
                    outputFile.getAbsolutePath()
            );

            LOGGER.info("Executing FFmpeg command: {}", String.join(" ", command));

            // Run FFmpeg
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    LOGGER.debug("FFmpeg log: {}", line); // changed to debug (less overhead)
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                LOGGER.error("FFmpeg process failed with exit code: {}", exitCode);
                return ResponseEntity.status(500)
                        .body(new InputStreamResource(
                                new ByteArrayInputStream(("FFmpeg failed with exit code " + exitCode).getBytes())
                        ));
            }

            LOGGER.info("Video compression completed successfully. Original size: {} KB, Compressed size: {} KB",
                    inputFile.length() / 1024, outputFile.length() / 1024);

            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime) / 1000;
            LOGGER.info("⏱️ Total API execution time: {} seconds", duration);

            // Return as streaming response (no memory overhead)
            InputStreamResource resource = new InputStreamResource(new FileInputStream(outputFile));

            // Clean up input file immediately
            if (inputFile.delete()) {
                LOGGER.info("Deleted temporary input file: {}", inputFile.getAbsolutePath());
            }

            // Delete output file on JVM exit (after response streaming completes)
            outputFile.deleteOnExit();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + outputFile.getName())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            LOGGER.error(" Total API execution time (failed): {} seconds", (endTime - startTime) / 1000);
            LOGGER.error("Error occurred while compressing video: {}", e.getMessage(), e);

            return ResponseEntity.status(500)
                    .body(new InputStreamResource(
                            new ByteArrayInputStream(("Error compressing video: " + e.getMessage()).getBytes())
                    ));
        }
    }
}
