package com.module.idw_signage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProgramScheduleDTO {

    private String programId;
    private List<TerminalDetails> terminalDetails;
//    private String[] storeIds;
//    private String[] terminalIds;
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
    private String[] weekDays;
//    @JsonProperty("isCommonPlaylist")
//    private String isCommonPlaylist; // "program", "commonPlaylist", "powerPlaylist"



}
