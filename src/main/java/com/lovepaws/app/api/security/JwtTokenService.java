package com.lovepaws.app.api.security;

import com.lovepaws.app.user.domain.Usuario;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class JwtTokenService {

    private static final String SECRET = "lovepaws-mobile-jwt-secret-change-me";
    private static final long EXPIRATION_SECONDS = 3600;

    public String generateToken(Usuario usuario) {
        long now = Instant.now().getEpochSecond();
        long exp = now + EXPIRATION_SECONDS;

        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String role = usuario.getRol() != null ? usuario.getRol().getNombre() : "ADOPTANTE";
        String payloadJson = String.format(
                "{\"sub\":\"%s\",\"uid\":%d,\"role\":\"%s\",\"iat\":%d,\"exp\":%d}",
                usuario.getUsername(),
                usuario.getId(),
                role,
                now,
                exp
        );

        String header = base64Url(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = base64Url(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signature = sign(header + "." + payload);

        return header + "." + payload + "." + signature;
    }

    public long getExpirationSeconds() {
        return EXPIRATION_SECONDS;
    }

    private String sign(String content) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return base64Url(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo firmar JWT", e);
        }
    }

    private String base64Url(byte[] input) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input);
    }
}
