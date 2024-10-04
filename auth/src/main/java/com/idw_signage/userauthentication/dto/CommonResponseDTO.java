package com.idw_signage.userauthentication.dto;

import lombok.Data;

@Data
public class CommonResponseDTO {

    private int statusCode; // 200
    private String status; // Created
    private String statusMessage; //User Created successfully
}
