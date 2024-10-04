package com.module.idw_signage.dto;

import lombok.Data;

@Data
public class TerminalRequestDTO {

    private String storeId;
    private String terminalName;
    private String terminalId;
    private String orientation;

}