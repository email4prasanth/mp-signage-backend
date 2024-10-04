package com.idw_signage.userauthentication.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class TokenResponseDto {

    private String access_token;
    private long expires_in;
    private String refresh_token;
    private long refresh_token_expires_in;
    private String role;
    private String userId;


}
