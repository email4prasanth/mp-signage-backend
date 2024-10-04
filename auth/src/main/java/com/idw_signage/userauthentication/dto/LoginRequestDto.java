package com.idw_signage.userauthentication.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class LoginRequestDto {
    private String username;
    private String password;

}

