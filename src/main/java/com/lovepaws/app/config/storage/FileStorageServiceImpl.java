package com.lovepaws.app.config.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.uploads.dir:uploads}")
    private String uploadsDirPath;

    @Value("${app.upload.max-size-bytes:2097152}") // 2MB por defecto
    private long maxFileSize;

    private Path uploadsDir;

    @PostConstruct
    public void init() {
        uploadsDir = Paths.get(uploadsDirPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadsDir);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear directorio uploads: " + uploadsDir, e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Validar tamaño
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("El archivo excede el tamaño máximo permitido (" + (maxFileSize/1024) + " KB).");
        }

        // Validar tipo MIME
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            throw new RuntimeException("Tipo de archivo no válido. Solo se permiten imágenes.");
        }

        // Determinar extensión
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        } else {
            // fallback based on contentType
            switch (contentType) {
                case "image/png": ext = ".png"; break;
                case "image/jpeg": ext = ".jpg"; break;
                case "image/gif": ext = ".gif"; break;
                default: ext = "";
            }
        }

        String filename = UUID.randomUUID().toString() + ext;
        Path target = uploadsDir.resolve(filename).normalize();

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error guardando archivo", e);
        }

        // Retornar ruta pública (servida por static/)
        return "/uploads/" + filename;
    }
}
