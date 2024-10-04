package com.module.idw_signage.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistDTO {

        private String id;
        private String storeID;
        private String programName;
        private String duration;
        private String size;
        private String startDate;
        private String endDate;
        private String prior;
        private String review;
        private String playFrequency;
    }


