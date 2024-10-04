package com.module.idw_signage.validation;

import com.module.idw_signage.Utils.Constants;
import com.module.idw_signage.dto.CreateProgramRequestDTO;
import com.module.idw_signage.dto.ProgramScheduleDTO;
import org.springframework.stereotype.Component;

@Component
public class ProgramValidation {

    public String saveProgramValidation(CreateProgramRequestDTO requestDTO) {
        if(requestDTO.getProgramName()  == null || requestDTO.getProgramName().isEmpty()) {
            return Constants.INVALID_PROGRAM_NAME;
        }else if(requestDTO.getStoreId() == null || requestDTO.getStoreId().isEmpty()) {
            return Constants.INVALID_STORE_ID;
        }else if(requestDTO.getLink() == null || requestDTO.getLink().isEmpty()) {
            return Constants.INVALID_LINK;
        }else {
            return null;
        }
    }

    public String createSchedulingValidation(ProgramScheduleDTO requestDTO) {
        if(requestDTO.getProgramId()  == null || requestDTO.getProgramId().isEmpty()) {
            return Constants.INVALID_PROGRAM_ID;
        } else if(requestDTO.getTerminalDetails().isEmpty()) {
            return Constants.INVALID_STORE_IDS;
        } else if(requestDTO.getTerminalDetails().stream()
                .anyMatch(terminalDetail -> terminalDetail.getTerminalList() == null || terminalDetail.getTerminalList().length == 0)) {
            return Constants.INVALID_TERMINAL_IDS;
        }
        else if(requestDTO.getScheduleFrequency() == null || requestDTO.getScheduleFrequency().isEmpty()) {
            return Constants.INVALID_SCHEDULE_FREQEUNCY;
        }else if(requestDTO.getStartDate() == null || requestDTO.getStartDate().isEmpty()) {
            return Constants.INVALID_START_DATE;
        } else if(requestDTO.getEndDate() == null || requestDTO.getEndDate().isEmpty()) {
            return Constants.INVALID_END_DATE;
        }else if(requestDTO.getPlayStartTime() == null || requestDTO.getPlayStartTime().isEmpty()) {
            return Constants.INVALID_PLAY_START_TIME;
        }else if(requestDTO.getPlayEndTime() == null || requestDTO.getPlayEndTime().isEmpty()) {
            return Constants.INVALID_PLAY_END_TIME;
        } else if(requestDTO.getPlayMode() == null || requestDTO.getPlayMode().isEmpty()) {
            return Constants.INVALID_PLAY_MODE;
        } else if(requestDTO.getTimings() == null || requestDTO.getTimings().isEmpty()) {
            return Constants.INVALID_TIMINGS;
        } else if(requestDTO.getProgramOverlap()  == null || requestDTO.getProgramOverlap().isEmpty()) {
            return Constants.INVALID_PROGRAM_OVERLAP;
        }else if(requestDTO.getDownloadTime() == null || requestDTO.getDownloadTime().isEmpty()) {
            return Constants.INVALID_DOWNLOAD_TIME;
        }else if(requestDTO.getDownloadStartTime() == null || requestDTO.getDownloadStartTime().isEmpty()) {
            return Constants.INVALID_DOWNLOAD_START_TIME;
        }else if(requestDTO.getDownloadEndTime() == null || requestDTO.getDownloadEndTime().isEmpty()) {
            return Constants.INVALID_DOWNLOAD_END_TIME;
        } else {
            return null;
        }
    }
}
