package com.example.youtubedownloader.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/download")
public class DownloadController {

    private final String DOWNLOAD_DIR = "downloads";
    private final String PYTHON_SCRIPT = "src/main/python/downloader.py";

    @GetMapping("")
    public ResponseEntity<Resource> downloadMedia(
            @RequestParam String url,
            @RequestParam String format) {
        
        System.out.println("Received download request for URL: " + url + " and format: " + format);

        if (!format.equalsIgnoreCase("mp3") && !format.equalsIgnoreCase("mp4")) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // Ensure download directory exists
            File dir = new File(DOWNLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Execute Python script
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "python", PYTHON_SCRIPT, url, format.toLowerCase(), DOWNLOAD_DIR);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String lastLine = "";
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                lastLine = line;
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            // Extract filename from the success message
            // Script prints: Success: path/to/file.ext
            if (lastLine.startsWith("Success: ")) {
                String filePath = lastLine.substring(9).trim();
                File file = new File(filePath);

                if (file.exists()) {
                    Resource resource = new FileSystemResource(file);
                    String contentType = format.equalsIgnoreCase("mp4") ? "video/mp4" : "audio/mpeg";

                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                            .body(resource);
                }
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
