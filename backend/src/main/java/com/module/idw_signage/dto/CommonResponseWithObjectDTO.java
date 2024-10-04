package com.module.idw_signage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CommonResponseWithObjectDTO {

    private int statusCode; // 200
    private String status; // Created
    private String statusMessage; //User Created successfully
    private Object data; //body content
    private int totalPages; // Total number of pages
    private long totalRecords;// Total number of elements

}
