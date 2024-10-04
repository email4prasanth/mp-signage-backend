package com.module.idw_signage.dto;

import lombok.Data;

@Data
public class UserRequestDTO {

    private  String id;
    private String fullname;
    private String email;
    private String access;
    private String[] stores;
    private String password;
}
