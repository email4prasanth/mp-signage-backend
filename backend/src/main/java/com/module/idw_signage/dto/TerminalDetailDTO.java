package com.module.idw_signage.dto;

import lombok.Data;

import java.util.List;

@Data
public class TerminalDetailDTO {
    private String storeId;
    private String storeName;
    private List<TerminalListDTO> terminalList;
}
