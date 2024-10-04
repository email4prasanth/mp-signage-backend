package com.idw_signage.userauthentication.serviceImpl;


import com.idw_signage.userauthentication.dto.CommonResponseDTO;
import com.idw_signage.userauthentication.dto.CommonResponseWithObjectDTO;
import com.idw_signage.userauthentication.dto.LoginRequestDto;
import com.idw_signage.userauthentication.dto.TokenResponseDto;
import com.idw_signage.userauthentication.entity.Users;
import com.idw_signage.userauthentication.repository.UserRepository;
import com.idw_signage.userauthentication.service.AuthService;
import com.idw_signage.userauthentication.utils.Constants;
import com.idw_signage.userauthentication.utils.JwtUtils;
import com.idw_signage.userauthentication.utils.TokenStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(UserRepository userRepository, JwtUtils jwtUtils,BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    public ResponseEntity<?> login(LoginRequestDto loginRequest) {
        logger.info("AuthServiceImpl:: login:: Enter into the method...");
        CommonResponseWithObjectDTO successResponse = new CommonResponseWithObjectDTO();
        CommonResponseDTO errorResponse = new CommonResponseDTO();

        try {
            if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
                errorResponse.setStatus(Constants.BAD_REQUEST_MESSAGE);
                errorResponse.setStatusCode(Constants.BAD_REQUEST);
                errorResponse.setStatusMessage(Constants.USERNAME_AND_PASSWORD_REQUIRED);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            Users user = userRepository.findByEmail(loginRequest.getUsername()).orElse(null);

            if (user == null) {
                errorResponse.setStatus(Constants.UNAUTHORIZED_MESSAGE);
                errorResponse.setStatusCode(Constants.UNAUTHORIZED);
                errorResponse.setStatusMessage(Constants.USER_NOT_FOUND);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            if (user.getStatus()!=null && user.getStatus().equalsIgnoreCase(Constants.INACTIVE)) {
                errorResponse.setStatus(Constants.UNAUTHORIZED_MESSAGE);
                errorResponse.setStatusCode(Constants.UNAUTHORIZED);
                errorResponse.setStatusMessage(Constants.USER_STATUS_INACTIVE);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            if (bCryptPasswordEncoder == null || !bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                errorResponse.setStatus(Constants.UNAUTHORIZED_MESSAGE);
                errorResponse.setStatusCode(Constants.UNAUTHORIZED);
                errorResponse.setStatusMessage(Constants.INVALID_USERNAME_OR_PASSWORD);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            TokenResponseDto tokenResponseDto = jwtUtils.generateJwtToken(user);

            user.setLastLogin(new Date());
            userRepository.save(user);

            if (tokenResponseDto == null) {
                errorResponse.setStatus(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
                errorResponse.setStatusCode(Constants.INTERNAL_SERVER_ERROR);
                errorResponse.setStatusMessage(Constants.FAILED_TO_GENERATE_TOKEN);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("access_token", tokenResponseDto.getAccess_token());
            data.put("expires_in", tokenResponseDto.getExpires_in());
            data.put("refresh_token", tokenResponseDto.getRefresh_token());
            data.put("refresh_token_expires_in", tokenResponseDto.getRefresh_token_expires_in());
            data.put("role", tokenResponseDto.getRole());
            data.put("userId", tokenResponseDto.getUserId());

            successResponse.setStatusCode(Constants.OK);
            successResponse.setStatus(Constants.OK_MESSAGE);
            successResponse.setStatusMessage(Constants.USER_VERIFIED_SUCESSFULLY);
            successResponse.setData(data);
            logger.info("AuthServiceImpl:: login:: User logged in and token generated...");

            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            logger.error("AuthServiceImpl:: login:: Exception occurred: {}", e.toString(), e);
            errorResponse.setStatus(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
            errorResponse.setStatusCode(Constants.INTERNAL_SERVER_ERROR);
            errorResponse.setStatusMessage(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    @Override
    public ResponseEntity<?> validateToken(String authorizationHeader) {
        logger.info("AuthServiceImpl:: validateToken:: Enter into the method...");

        CommonResponseWithObjectDTO successResponse = new CommonResponseWithObjectDTO();
        CommonResponseDTO errorResponse = new CommonResponseDTO();

        try {
            Map<String, Object> response = jwtUtils.validateToken(authorizationHeader);
            String status = (String) response.get(Constants.STATUS);

            if (status.equalsIgnoreCase(String.valueOf(TokenStatus.VALIDATED))) {
                String userId = (String) response.get("userId");
                String fullName = (String) response.get("fullName");

                Optional<Users> optionalUser = userRepository.findById(userId);

                if (optionalUser.isPresent()) {
                    Users user = optionalUser.get();
                    String role = user.getRole();
                    response.put("userId", userId);
                    response.put("fullName", fullName);
                    response.put("role", role);
                    logger.info("AuthServiceImpl:: validateToken:: Token validated successfully for userId: {}", userId);
                } else {
                    logger.warn("AuthServiceImpl:: validateToken:: User not found for userId: {}", userId);
                }
            } else {
                errorResponse.setStatus(Constants.UNAUTHORIZED_MESSAGE);
                errorResponse.setStatusCode(Constants.UNAUTHORIZED);
                errorResponse.setStatusMessage((String) response.get(Constants.MESSAGE));
                logger.warn("AuthServiceImpl:: validateToken:: Token validation failed: {}", response.get(Constants.MESSAGE));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            successResponse.setStatusCode(Constants.OK);
            successResponse.setStatus(Constants.OK_MESSAGE);
            successResponse.setStatusMessage(Constants.TOKEN_VALIDATED);
            successResponse.setData(response);
            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            logger.error("AuthServiceImpl:: validateToken:: Exception occurred: {}", e.toString(), e);
            errorResponse.setStatus(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
            errorResponse.setStatusCode(Constants.INTERNAL_SERVER_ERROR);
            errorResponse.setStatusMessage(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    @Override
    public ResponseEntity<?> validateRefreshToken(String refreshToken) {
        logger.info("AuthServiceImpl:: validateRefreshToken:: Enter into the method...");

        CommonResponseWithObjectDTO successResponse = new CommonResponseWithObjectDTO();
        CommonResponseDTO errorResponse = new CommonResponseDTO();

        try {
            Map<String, Object> response = jwtUtils.validateRefreshToken(refreshToken);
            String status = (String) response.get(Constants.STATUS);

            if (status.equalsIgnoreCase(String.valueOf(TokenStatus.VALIDATED))) {
                String userId = (String) response.get("userId");

                Optional<Users> optionalUser = userRepository.findById(userId);
                if (optionalUser.isPresent()) {
                    Users user = optionalUser.get();

                    TokenResponseDto tokenResponse = jwtUtils.generateJwtToken(user);

                    Map<String, Object> tokenResponseMap = new HashMap<>();
                    tokenResponseMap.put("access_token", tokenResponse.getAccess_token());
                    tokenResponseMap.put("expires_in", tokenResponse.getExpires_in());
                    tokenResponseMap.put("refresh_token", tokenResponse.getRefresh_token());
                    tokenResponseMap.put("refresh_token_expires_in", tokenResponse.getRefresh_token_expires_in());
                    tokenResponseMap.put("userId", tokenResponse.getUserId());
                    tokenResponseMap.put("role", tokenResponse.getRole());

                    successResponse.setStatusCode(Constants.OK);
                    successResponse.setStatus(Constants.OK_MESSAGE);
                    successResponse.setStatusMessage((String) response.get(Constants.MESSAGE));
                    successResponse.setData(tokenResponseMap);

                    logger.info("AuthServiceImpl:: validateRefreshToken:: Refresh token validated and new tokens generated for userId: {}", userId);
                    return ResponseEntity.ok(successResponse);
                } else {
                    errorResponse.setStatusCode(Constants.UNAUTHORIZED);
                    errorResponse.setStatus(Constants.UNAUTHORIZED_MESSAGE);
                    errorResponse.setStatusMessage(Constants.USER_NOT_FOUND);
                    logger.warn("AuthServiceImpl:: validateRefreshToken:: User not found for userId: {}", userId);
                }
            } else {
                String message = (String) response.get(Constants.MESSAGE);
                errorResponse.setStatusCode(Constants.UNAUTHORIZED);
                errorResponse.setStatus(Constants.UNAUTHORIZED_MESSAGE);
                errorResponse.setStatusMessage(Constants.INVALID_REFRESH);
                logger.warn("AuthServiceImpl:: validateRefreshToken:: Invalid refresh token: {}", message);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (Exception e) {
            logger.error("AuthServiceImpl:: validateRefreshToken:: Exception occurred: {}", e.toString(), e);
            errorResponse.setStatus(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
            errorResponse.setStatusCode(Constants.INTERNAL_SERVER_ERROR);
            errorResponse.setStatusMessage(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


}