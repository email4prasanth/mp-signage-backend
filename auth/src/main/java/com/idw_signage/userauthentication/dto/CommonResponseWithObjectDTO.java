package com.idw_signage.userauthentication.dto;

import lombok.Data;

@Data
public class CommonResponseWithObjectDTO {

    private int statusCode; // 200statusCode
    private String status; // Created
    private String statusMessage; //User Created successfully
    private Object data; //body content

}
