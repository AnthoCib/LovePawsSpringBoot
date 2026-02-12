package com.lovepaws.app.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginResponseDTO {
    String token;
    String tokenType;
    Long expiresIn;
    String username;
    String role;
}
