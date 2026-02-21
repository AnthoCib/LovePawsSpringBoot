package com.lovepaws.app.config.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

	 /**
     * Guarda la imagen y devuelve la URL pública (ej: /uploads/abc123.jpg)
     */
	String store(MultipartFile file);
    
   
}
