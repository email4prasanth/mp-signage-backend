package com.module.idw_signage.controlller;


import com.module.idw_signage.dto.CommonResponseWithObjectDTO;
import com.module.idw_signage.dto.TerminalRequestDTO;
import com.module.idw_signage.service.TerminalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/services")

public class TerminalController {
    private static final Logger logger = LoggerFactory.getLogger(TerminalController.class);

    @Autowired
    private TerminalService terminalService;

    @PostMapping("/terminal")
    public ResponseEntity<?> createTerminal(@RequestHeader("Authorization") String accessToken,@RequestBody TerminalRequestDTO terminalRequestDTO) {
        try {
            return terminalService.saveTerminal(terminalRequestDTO);
        } catch (RuntimeException e) {
            logger.error("e: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/terminals/{storeId}")
    public ResponseEntity<?> getTerminals(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("storeId") String storeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
    @RequestParam(required = false) String search) {
        return terminalService.getTerminals(storeId,page,limit,search);
    }

}
