package com.module.idw_signage.controlller;

import com.module.idw_signage.dto.CreateProgramRequestDTO;
import com.module.idw_signage.dto.ProgramScheduleDTO;
import com.module.idw_signage.service.ProgramService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/services")
public class ProgramController {

    private static final Logger logger = LoggerFactory.getLogger(ProgramController.class);

    @Autowired
    private ProgramService programService;

    @PostMapping("/program")
    public ResponseEntity<?> saveProgram(@RequestHeader("Authorization") String accessToken, @RequestBody CreateProgramRequestDTO createProgramRequestDTO, HttpServletRequest request) {
        logger.info("ProgramController:: saveProgram:: Enter into the method...");
        String role = request.getAttribute("role").toString();
        return programService.createProgram(createProgramRequestDTO, request.getAttribute("userId").toString(),role);
    }

    @GetMapping("/program/{storeId}")
    public ResponseEntity<?> getProgram(@RequestHeader("Authorization") String accessToken,
                                        HttpServletRequest request,
                                        @PathVariable("storeId") String storeId) {
        logger.info("ProgramController:: getProgram:: Enter into the method...");

        return programService.getProgram(storeId, request.getAttribute("userId").toString());
    }

    @PostMapping("/program/scheduling")
    public ResponseEntity<?> saveProgramSchedule(@RequestHeader("Authorization") String accessToken, @RequestBody ProgramScheduleDTO programScheduleDTO, HttpServletRequest request) {
        logger.info("ProgramController:: saveProgramSchedule:: Enter into the method...");

        return programService.createProgramScheduling(programScheduleDTO, request.getAttribute("userId").toString());
    }

    @GetMapping("/general/scheduling/{programId}")
    public ResponseEntity<?> getProgramSchedule(@RequestHeader("Authorization") String accessToken,
                                                HttpServletRequest request,
                                                @PathVariable("programId") String programId,
                                                @RequestParam("action") String action ) {
        logger.info("ProgramController:: getProgramSchedule:: Enter into the method...");

        return programService.getProgramSchedule(programId, request.getAttribute("userId").toString(),action);
    }

}
