package com.module.idw_signage.service;

import com.module.idw_signage.dto.TerminalRequestDTO;
import org.springframework.http.ResponseEntity;

public interface TerminalService {
    ResponseEntity<?> saveTerminal(TerminalRequestDTO terminalRequestDTO);

    ResponseEntity<?> getTerminals(String storeId, int page, int limit,String search);
}
