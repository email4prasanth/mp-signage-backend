package com.module.idw_signage.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.module.idw_signage.dto.ProgramSchedulerDTO;
import com.module.idw_signage.dto.TerminalDetailDTO;
import com.module.idw_signage.dto.TerminalListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProgramSchedulerMapper {
    private static final Logger logger = LoggerFactory.getLogger(ProgramSchedulerMapper.class);

    public ProgramSchedulerDTO mapToDTO(List<Object[]> details) {
        ProgramSchedulerDTO dto = new ProgramSchedulerDTO();

        try {
            if (details != null && !details.isEmpty()) {
                Object[] firstRecord = details.get(0);

                try {
                    dto.setScheduleFrequency(safeCastToString(firstRecord[2]));
                    dto.setStartDate(formatTimestamp(firstRecord[3]));
                    dto.setEndDate(formatTimestamp(firstRecord[4]));
                    dto.setPlayStartTime(safeCastToString(firstRecord[5]));
                    dto.setPlayEndTime(safeCastToString(firstRecord[6]));
                    dto.setPlayMode(safeCastToString(firstRecord[7]));
                    dto.setTimings(safeCastToString(firstRecord[8]));
                    dto.setProgramOverlap(safeCastToString(firstRecord[9]));
                    dto.setDownloadTime(safeCastToString(firstRecord[10]));
                    dto.setDownloadStartTime(safeCastToString(firstRecord[11]));
                    dto.setDownloadEndTime(safeCastToString(firstRecord[12]));
                    dto.setId(safeCastToString(firstRecord[14]));
                    ObjectMapper objectMapper = new ObjectMapper();
                    dto.setWeekDays(objectMapper.readValue(safeCastToString(firstRecord[17]), String[].class));
                } catch (ClassCastException e) {
                    logger.error("Error casting schedule fields: {}", e.getMessage());
                }

                try {
                    String storeMappings = safeCastToString(firstRecord[15]);
                    String terminalMappings = safeCastToString(firstRecord[16]);

                    List<TerminalDetailDTO> terminalDetails = parseStoreMappings(storeMappings, terminalMappings);
                    dto.setTerminalDetails(terminalDetails);
                } catch (Exception e) {
                    logger.error("Error parsing store or terminal mappings: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Error processing ProgramSchedulerDTO mapping: {}", e.getMessage());
        }

        return dto;
    }

    private String formatTimestamp(Object obj) {
        try {
            if (obj instanceof Timestamp) {
                return ((Timestamp) obj).toString();
            }
        } catch (Exception e) {
            logger.error("Error formatting Timestamp: {}", e.getMessage());
        }
        return null;
    }

    private String safeCastToString(Object obj) {
        try {
            return obj != null ? obj.toString() : null;
        } catch (Exception e) {
            logger.error("Error casting to String: {}", e.getMessage());
            return null;
        }
    }

    private List<TerminalDetailDTO> parseStoreMappings(String storeMappings, String terminalMappings) {
        List<TerminalDetailDTO> terminalDetails = new ArrayList<>();
        try {
            if (storeMappings != null) {
                List<String> storeEntries = Arrays.asList(storeMappings.split("\\$#\\$"));

                for (String storeEntry : storeEntries) {
                    String[] storeParts = storeEntry.split(":\\$:");
                    if (storeParts.length == 3) {
                        String storeId = storeParts[0];
                        String storeName = storeParts[1];
                        String programScheduleStoreId = storeParts[2];

                        TerminalDetailDTO terminalDetailDTO = new TerminalDetailDTO();
                        terminalDetailDTO.setStoreId(storeId);
                        terminalDetailDTO.setStoreName(storeName);

                        List<TerminalListDTO> terminalList = parseTerminalMappings(programScheduleStoreId, terminalMappings);
                        terminalDetailDTO.setTerminalList(terminalList);

                        terminalDetails.add(terminalDetailDTO);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error parsing store mappings: {}", e.getMessage());
        }

        return terminalDetails;
    }

    private List<TerminalListDTO> parseTerminalMappings(String programScheduleStoreId, String terminalMappings) {
        List<TerminalListDTO> terminalList = new ArrayList<>();
        try {
            if (terminalMappings != null) {
                List<String> terminalEntries = Arrays.asList(terminalMappings.split("\\$#\\$"));

                for (String terminalEntry : terminalEntries) {
                    String[] terminalParts = terminalEntry.split(":\\$:");
                    if (terminalParts.length == 3 && terminalParts[0].equals(programScheduleStoreId)) {
                        String terminalId = terminalParts[1];
                        String terminalName = terminalParts[2];

                        TerminalListDTO terminalListDTO = new TerminalListDTO();
                        terminalListDTO.setTerminalId(terminalId);
                        terminalListDTO.setTerminalName(terminalName);

                        terminalList.add(terminalListDTO);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error parsing terminal mappings: {}", e.getMessage());
        }

        return terminalList;
    }
}
