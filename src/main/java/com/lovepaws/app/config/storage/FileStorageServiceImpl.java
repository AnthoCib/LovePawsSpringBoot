package com.lovepaws.app.config.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final Cloudinary cloudinary;

    @Value("${app.upload.max-size-bytes:26214400}")
    private long maxFileSize;

    @Override
    public String store(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        // Validar tamaño
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("El archivo excede el tamaño máximo permitido.");
        }

        // Validar tipo MIME
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            throw new RuntimeException("Tipo de archivo no válido. Solo imágenes.");
        }

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "lovepaws",
                            "public_id", UUID.randomUUID().toString()
                    )
            );

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Error subiendo imagen a Cloudinary", e);
        }
    }
}