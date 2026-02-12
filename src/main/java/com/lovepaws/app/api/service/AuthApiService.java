package com.lovepaws.app.api.service;

import com.lovepaws.app.api.dto.LoginRequestDTO;
import com.lovepaws.app.api.dto.LoginResponseDTO;

public interface AuthApiService {
    LoginResponseDTO login(LoginRequestDTO request);
}
