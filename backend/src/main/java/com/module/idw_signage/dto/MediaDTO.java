package com.module.idw_signage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class MediaDTO {

    private String mediaId;
    private String storeID;
    private String fileName;
    private Integer size;
    private String duration;
    private String uploader;
    private String expiryDate;
    private String link;
    private String checker;
    private String status;

}
