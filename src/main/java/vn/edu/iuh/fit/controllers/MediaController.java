/*
 * @ (#) MediaController.java    1.0    19/03/2025
 * Copyright (c) 2025 IUH. All rights reserved.
 */
package vn.edu.iuh.fit.controllers;/*
 * @description:
 * @author: Bao Thong
 * @date: 19/03/2025
 * @version: 1.0
 */

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.iuh.fit.services.CloudinaryService;

import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class MediaController {

    private final CloudinaryService cloudinaryService;

    public MediaController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("userId") UUID userId,
                                        @RequestParam(value = "associatedIDMessageId", required = false) Long associatedIDMessageId) {
        try {
            String url = cloudinaryService.uploadFileAndSaveToDB(file, userId, associatedIDMessageId);
            return ResponseEntity.ok("File uploaded to: " + url);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }
}
