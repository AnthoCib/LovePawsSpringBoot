package com.lovepaws.app.config.storage;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryImageService implements FileStorageService {

	 private final Cloudinary cloudinary;
	 
	
	@Override
	public String store(MultipartFile file) {
		 if (file == null || file.isEmpty()) {
	            return null;
	        }

	        try {
	            Map uploadResult = cloudinary.uploader().upload(
	                file.getBytes(),
	                ObjectUtils.asMap("folder", "lovepaws")
	            );

	            System.out.println("Resultado Cloudinary: " + uploadResult);

	            return uploadResult.get("secure_url").toString();

	        } catch (IOException e) {
	            throw new RuntimeException("Error subiendo imagen a Cloudinary", e);
	        }
	    }

}
