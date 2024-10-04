package com.module.idw_signage.dto;

import lombok.Data;

@Data
public class UsersResponseDTO {

    private String userId;
    private String username;
    private String email;
    private String access;
    private String lastLogin;
    private String dateAdded;

}
