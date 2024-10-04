package com.module.idw_signage.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProgramSchedulerDTO {
    private List<TerminalDetailDTO> terminalDetails;
    private String scheduleFrequency;
    private String startDate;
    private String endDate;
    private String playStartTime;
    private String playEndTime;
    private String playMode;
    private String timings;
    private String programOverlap;
    private String downloadTime;
    private String downloadStartTime;
    private String downloadEndTime;
    private String id;
    private String[] weekDays;
}
