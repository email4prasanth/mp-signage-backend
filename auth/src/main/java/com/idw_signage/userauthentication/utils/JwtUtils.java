package com.idw_signage.userauthentication.utils;

import com.idw_signage.userauthentication.dto.TokenResponseDto;
import com.idw_signage.userauthentication.entity.Users;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.refresh.secret}")
    private String jwtRefreshTokenSecret;
    @Value("${jwt.expiration}")
    private int jwtExpiration;
    @Value("${jwt.refresh.expiration}")
    private int jwtRefreshTokenExpiration;

//    public static final String jwtSecret = System.getenv("JWT_SECRET_KEY");
//    public static final String jwtRefreshTokenSecret = System.getenv("JWT_REFRESH_SECRET_KEY");
//    public static final int jwtExpiration = System.getenv("JWT_TOKEN_EXPIRATION_IN_SECONDS") != null ? Integer.parseInt(System.getenv("JWT_TOKEN_EXPIRATION_IN_SECONDS")) : 0;
//    public static final int jwtRefreshTokenExpiration = System.getenv("JWT_REFRESH_TOKEN_EXPIRATION_IN_SECONDS") != null ? Integer.parseInt(System.getenv("JWT_REFRESH_TOKEN_EXPIRATION_IN_SECONDS")) : 0;
//	public static final int jwtExpiration = 1800;
//	public static final int jwtRefreshTokenExpiration = 3600;

    public TokenResponseDto generateJwtToken(Users user) {
        TokenResponseDto tokenResponseDto = new TokenResponseDto();

        Date expirationDate = new Date((new Date()).getTime() + jwtExpiration * 1000);
        Date refreshTokenExpirationDate = new Date((new Date()).getTime() + jwtRefreshTokenExpiration * 1000);

        String token = Jwts.builder().setSubject(user.getId().toString()).claim("fullName", user.getFullname()).claim("userId", user.getId()).setIssuedAt(new Date()).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes(Charset.forName("UTF-8"))).compact();

        String refreshToken = Jwts.builder().setSubject(user.getId().toString()).claim("fullName", user.getFullname()).claim("userId", user.getId()).setIssuedAt(new Date()).setExpiration(refreshTokenExpirationDate).signWith(SignatureAlgorithm.HS256, jwtRefreshTokenSecret.getBytes(Charset.forName("UTF-8"))).compact();

        tokenResponseDto.setAccess_token(token);
        tokenResponseDto.setExpires_in(expirationDate.getTime());
        tokenResponseDto.setRefresh_token(refreshToken);
        tokenResponseDto.setRefresh_token_expires_in(refreshTokenExpirationDate.getTime());
        tokenResponseDto.setUserId(String.valueOf(user.getId()));
        tokenResponseDto.setRole(user.getRole());

        return tokenResponseDto;
    }

    public Map<String, Object> validateToken(String authorizationHeader) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.put(Constants.STATUS, Constants.INVALID);
            response.put(Constants.MESSAGE, Constants.HEADER_TOKEN_MISSING);

            return response;
        }

        String token = authorizationHeader.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret.getBytes(Charset.forName("UTF-8")))
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.get("userId").toString();
            String fullName = claims.get("fullName", String.class);

            response.put(Constants.STATUS, Constants.VALIDATED);
            response.put(Constants.USER_ID, userId);
            response.put(Constants.FULLNAME, fullName);

        } catch (SignatureException e) {
            response.put(Constants.STATUS, Constants.INVALID);
            response.put(Constants.MESSAGE, Constants.INVALID_JWT_SIGNATURE);

        } catch (MalformedJwtException e) {
            response.put(Constants.STATUS, Constants.INVALID);
            response.put(Constants.MESSAGE, Constants.INVALID_JWT_TOKEN);

        } catch (ExpiredJwtException e) {
            response.put(Constants.STATUS, Constants.INVALID);
            response.put(Constants.MESSAGE, Constants.JWT_TOKEN_EXPIRED);

        } catch (UnsupportedJwtException e) {
            response.put(Constants.STATUS, Constants.INVALID);
            response.put(Constants.MESSAGE, Constants.JWT_TOKEN_UNSUPPORTED);

        } catch (IllegalArgumentException e) {
            response.put(Constants.STATUS, Constants.INVALID);
            response.put(Constants.MESSAGE, Constants.JWT_CLAIM_STRING_EMPTY);

        }

        return response;
    }



    public Map<String, Object> validateRefreshToken(String refreshToken) {
        Map<String, Object> response = new HashMap<>();

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtRefreshTokenSecret.getBytes(Charset.forName("UTF-8")))
                    .parseClaimsJws(refreshToken)
                    .getBody();

            String userId = claims.get("userId").toString();
            String fullName = claims.get("fullName", String.class);
            Date expiration = claims.getExpiration();

            if (expiration.before(new Date())) {
                response.put(Constants.STATUS, Constants.EXPIRED);
                response.put(Constants.MESSAGE, Constants.REFRESH_TOKEN_EXPIRED);
            } else {
                response.put(Constants.STATUS, Constants.VALIDATED);
                response.put(Constants.USER_ID, userId);
                response.put(Constants.FULLNAME, fullName);
                response.put("expiration", expiration);
            }

        } catch (ExpiredJwtException e) {
            response.put(Constants.STATUS, Constants.EXPIRED);
            response.put(Constants.MESSAGE,Constants.REFRESH_TOKEN_EXPIRED);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            response.put(Constants.STATUS, Constants.INVALID);
            response.put(Constants.MESSAGE, Constants.INVALID_REFRESH);
        }
        response.put(Constants.MESSAGE, Constants.TOKENS_GENERATED);
        return response;
    }



}




