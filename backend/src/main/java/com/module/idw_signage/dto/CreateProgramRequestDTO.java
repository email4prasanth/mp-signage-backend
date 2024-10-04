package com.module.idw_signage.dto;

import lombok.Data;

@Data
public class CreateProgramRequestDTO {

    private String storeId;
    private String programName;
    private String status;
    private String expiryDate;
    private String filename;
    private String link;
    private String format;
    private String duration;
    private String fileSize;

}
