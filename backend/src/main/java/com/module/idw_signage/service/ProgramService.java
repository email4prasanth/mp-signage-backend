package com.module.idw_signage.service;

import com.module.idw_signage.dto.CreateProgramRequestDTO;
import com.module.idw_signage.dto.ProgramScheduleDTO;
import org.springframework.http.ResponseEntity;

public interface ProgramService {
    ResponseEntity<?> createProgram(CreateProgramRequestDTO createProgramRequestDTO, String userId, String role);

    ResponseEntity<?> getProgram(String storeId, String userId);

    ResponseEntity<?> createProgramScheduling(ProgramScheduleDTO programScheduleDTO, String userId);

    ResponseEntity<?> getProgramSchedule(String programId, String userId, String action);

}
