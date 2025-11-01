package com.blog.controller;

import com.blog.dto.ApiResponse;
import com.blog.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    /**
     * Upload image file
     */
    @PostMapping("/image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Please select a file to upload"));
            }

            // Validate file type
            if (!fileStorageService.isValidImageFile(file)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Only image files (JPEG, PNG, GIF, WebP) are allowed"));
            }

            // Validate file size (10MB max)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("File size must not exceed 10MB"));
            }

            // Store file
            String fileName = fileStorageService.storeFile(file);
            String fileUrl = "/api/upload/images/" + fileName;

            Map<String, String> response = new HashMap<>();
            response.put("fileName", fileName);
            response.put("fileUrl", fileUrl);
            response.put("fileType", file.getContentType());
            response.put("fileSize", String.valueOf(file.getSize()));

            log.info("File uploaded successfully: {}", fileName);
            return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", response));
        } catch (Exception e) {
            log.error("Error uploading file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error uploading file: " + e.getMessage()));
        }
    }

    /**
     * Get/Download uploaded image
     */
    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("./uploads").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Determine content type
            String contentType = "application/octet-stream";
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (fileName.endsWith(".png")) {
                contentType = "image/png";
            } else if (fileName.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (fileName.endsWith(".webp")) {
                contentType = "image/webp";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("Error retrieving file: {}", fileName, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete uploaded image
     */
    @DeleteMapping("/images/{fileName:.+}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable String fileName) {
        try {
            fileStorageService.deleteFile(fileName);
            return ResponseEntity.ok(ApiResponse.success("File deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting file: {}", fileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error deleting file: " + e.getMessage()));
        }
    }
}
