package com.idw_signage.userauthentication.controller;
import com.idw_signage.userauthentication.dto.LoginRequestDto;
import com.idw_signage.userauthentication.dto.TokenResponseDto;
import com.idw_signage.userauthentication.service.AuthService;
import com.idw_signage.userauthentication.utils.AlertConstants;
import com.idw_signage.userauthentication.utils.TokenStatus;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/services")
@RestController
public class UserController {

    @Autowired
    private LoginRequestDto loginRequestDto;

    @Autowired
    private AuthService authService;

    @GetMapping("/test")
    public String test() {
        return "test works";
    }

    @PostMapping("/authToken")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            ResponseEntity<?> tokenResponseDto = authService.login(loginRequest);
            return tokenResponseDto;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return authService.validateToken(authorizationHeader);

    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody String requestBody) throws JSONException {
        JSONObject request= new JSONObject(requestBody);
        String refreshToken = request.getString("refreshToken");
        return authService.validateRefreshToken(refreshToken);
    }


}
