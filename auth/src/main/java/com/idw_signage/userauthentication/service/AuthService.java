package com.idw_signage.userauthentication.service;

import com.idw_signage.userauthentication.dto.LoginRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AuthService {
    ResponseEntity<?> login(LoginRequestDto loginRequest) throws Exception;

    ResponseEntity<?> validateToken(String token);

    ResponseEntity<?> validateRefreshToken(String refreshToken);
}
