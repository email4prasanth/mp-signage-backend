package com.module.idw_signage.dto;

import lombok.Data;

@Data
public class ProgramDTO {
    private String id;
    private String programName;
    private String status;
    private String link;
    private String fileName;
    private String expiryDate;
    private String format;
    private String duration;
    private String fileSize;
    private String role;
}

