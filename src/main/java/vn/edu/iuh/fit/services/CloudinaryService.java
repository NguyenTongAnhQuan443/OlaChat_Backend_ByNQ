/*
 * @ (#) CloudinaryService.java    1.0    19/03/2025
 * Copyright (c) 2025 IUH. All rights reserved.
 */
package vn.edu.iuh.fit.services;/*
 * @description:
 * @author: Bao Thong
 * @date: 19/03/2025
 * @version: 1.0
 */

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.iuh.fit.models.Media;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.MediaRepository;
import vn.edu.iuh.fit.repositories.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final MediaRepository mediaRepository;

    @Autowired
    private UserRepository userRepository;

    public CloudinaryService(Cloudinary cloudinary, MediaRepository mediaRepository) {
        this.cloudinary = cloudinary;
        this.mediaRepository = mediaRepository;
    }

    public String uploadFileAndSaveToDB(MultipartFile file, UUID userId, Long associatedIDMessageId) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader()
                .upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));

        String url = uploadResult.get("secure_url").toString();
        User uploadedBy = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        Media media = Media.builder()
                .fileUrl(url)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .uploadedAt(LocalDateTime.now())
                .uploadedBy(uploadedBy)
                .associatedIDMessageId(associatedIDMessageId)
                .build();
        mediaRepository.save(media);
        return url;
    }
}