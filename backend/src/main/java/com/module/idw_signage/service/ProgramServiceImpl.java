package com.module.idw_signage.service;

import com.module.idw_signage.Utils.Constants;
import com.module.idw_signage.Utils.ProgramSchedulerMapper;
import com.module.idw_signage.dto.*;
import com.module.idw_signage.model.*;
import com.module.idw_signage.repository.*;
import com.module.idw_signage.validation.ProgramValidation;
import jakarta.persistence.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProgramServiceImpl implements ProgramService {

    private static final Logger logger = LoggerFactory.getLogger(ProgramServiceImpl.class);

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ProgramValidation programValidation;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProgramSchedulerRepository programScheduleRepository;

    @Autowired
    private ProgramSchedulerStoreDetailsRepository programScheduleStoreMappingRepository;

    @Autowired
    private ProgramSchedulerTerminalDetailsRepository programScheduleTerminalMappingRepository;

    @Autowired
    private TerminalRepository terminalRepository;

    @Autowired
    private ProgramSchedulerMapper mapper;

    @Autowired
    private CommonPlaylistRepository commonPlaylistRepository;

    @Autowired
    private CommonPlaylistSchedulerRepository commonPlaylistSchedulerRepository;

    @Autowired
    private PowerPlaylistRepository powerPlaylistRepository;

    @Autowired
    private PowerPlaylistSchedulerRepository powerPlaylistSchedulerRepository;

    @Override
    public ResponseEntity<?> createProgram(CreateProgramRequestDTO requestDTO, String userId, String role) {
        logger.info("ProgramServiceImpl:: createProgram:: Enter into the method...");
        try {
            String validation = programValidation.saveProgramValidation(requestDTO);
            CommonResponseDTO response = new CommonResponseDTO();
            if (validation != null) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(validation);
                return ResponseEntity.status(Constants.BAD_REQUEST).body(response);
            }
            Optional<Stores> optionalStores = storeRepository.findById(requestDTO.getStoreId());

            Optional<Program> programExistCheck = programRepository.findByProgramName(requestDTO.getProgramName());
            if (programExistCheck.isPresent()) {
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatusMessage(Constants.PROGRAM_ALREADY_EXIST);
                return ResponseEntity.status(Constants.BAD_REQUEST).body(response);
            }

            if (optionalStores.isPresent()) {

                Stores store = optionalStores.get();

                Program program = new Program();
                program.setStores(store);
                program.setProgramName(requestDTO.getProgramName());
                program.setStatus(requestDTO.getStatus());
                program.setLink(requestDTO.getLink());
                program.setFileName(requestDTO.getFilename());
                if ((requestDTO.getExpiryDate() != null || !requestDTO.getExpiryDate().equals(""))) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    program.setExpiryDate(formatter.parse(requestDTO.getExpiryDate()));
                } else {
                    program.setExpiryDate(null);
                }
                program.setFormat(requestDTO.getFormat());
                program.setDuration(requestDTO.getDuration());
                program.setFileSize(requestDTO.getFileSize());
                program.setRole(role);
                program.setUpdatedBy(userId);
                program.setCreatedBy(userId);

                Program programEntity = programRepository.save(program);

                if (programEntity.getId() != null) {
                    response.setStatusCode(Constants.CREATED);
                    response.setStatus(Constants.CREATED_MESSAGE);
                    response.setStatusMessage(Constants.PROGRAM_CREATED_SUCCESSFULLY);
                    return ResponseEntity.status(Constants.CREATED).body(response);
                } else {
                    response.setStatusCode(Constants.INTERNAL_SERVER_ERROR);
                    response.setStatus(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
                    response.setStatusMessage(Constants.EXCEPTION_MADE);
                    return ResponseEntity.status(Constants.INTERNAL_SERVER_ERROR).body(response);
                }
            } else {
                response.setStatus(Constants.NOT_FOUND_MESSAGE);
                response.setStatusCode(Constants.NOT_FOUND);
                response.setStatusMessage(Constants.STORE_NOT_FOUND_ID + requestDTO.getStoreId());
                return ResponseEntity.status(Constants.NOT_FOUND).body(response);
            }
        } catch (ParseException parseException) {
            logger.error("ProgramServiceImpl:: createProgram:: Enter into the method...", parseException);
            parseException.printStackTrace(); // Handle the exception
            return null;
        } catch (Exception exception) {
            logger.error("ProgramServiceImpl:: createProgram:: Enter into the method...", exception);
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public ResponseEntity<?> getProgram(String storeId, String userId) {

        try {
            Optional<Stores> optionalStore = storeRepository.findById(storeId);

            if (optionalStore.isPresent()) {
                List<Program> programs = programRepository.findByStoresId(storeId);

                if (!programs.isEmpty()) {
                    List<ProgramDTO> programDTOs = programs.stream().map(program -> {
                        ProgramDTO programDTO = new ProgramDTO();
                        programDTO.setId(program.getId());
                        programDTO.setProgramName(program.getProgramName());
                        programDTO.setStatus(program.getStatus());
                        programDTO.setLink(program.getLink());
                        programDTO.setFileName(program.getFileName());
                        programDTO.setExpiryDate(String.valueOf(program.getExpiryDate()));
                        programDTO.setFormat(program.getFormat());
                        programDTO.setDuration(program.getDuration());
                        programDTO.setFileSize(program.getFileSize());
                        programDTO.setRole(program.getRole());
                        return programDTO;
                    }).collect(Collectors.toList());

                    CommonResponseWithObjectDTO successResponse = new CommonResponseWithObjectDTO();
                    successResponse.setStatusCode(Constants.OK);
                    successResponse.setStatus(Constants.OK_MESSAGE);
                    successResponse.setStatusMessage(Constants.PROGRAM_FETCHED_SUCCESSFULLY);
                    successResponse.setData(programDTOs);

                    return ResponseEntity.status(HttpStatus.OK).body(successResponse);
                } else {
                    CommonResponseDTO response = new CommonResponseDTO();
                    response.setStatusCode(Constants.NOT_FOUND);
                    response.setStatus(Constants.NOT_FOUND_MESSAGE);
                    response.setStatusMessage(Constants.NO_PROGRAMS_FOUND_FOR_STORE + storeId);

                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                CommonResponseDTO response = new CommonResponseDTO();
                response.setStatusCode(Constants.NOT_FOUND);
                response.setStatus(Constants.NOT_FOUND_MESSAGE);
                response.setStatusMessage(Constants.STORE_NOT_FOUND_ID + storeId);

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception exception) {
            logger.error("ProgramServiceImpl:: getAllProgramsByStoreId:: Exception occurred...", exception);
            CommonResponseDTO errorResponse = new CommonResponseDTO();
            errorResponse.setStatusCode(Constants.INTERNAL_SERVER_ERROR);
            errorResponse.setStatus(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
            errorResponse.setStatusMessage(Constants.EXCEPTION_MADE);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> createProgramScheduling(ProgramScheduleDTO requestDTO, String userId) {
        logger.info("ProgramServiceImpl:: createProgramScheduling:: Enter into the method...");
        try {
            String validation = programValidation.createSchedulingValidation(requestDTO);
            CommonResponseDTO response = new CommonResponseDTO();
            if (validation != null) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(validation);
                return ResponseEntity.status(Constants.BAD_REQUEST).body(response);
            }

            if (!programRepository.existsById(requestDTO.getProgramId())) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(Constants.PROGRAM_NOT_FOUND);
                return ResponseEntity.status(Constants.BAD_REQUEST).body(response);
            }

//            if (programScheduleRepository.existsByProgramIdAndIsCommonPlaylist(requestDTO.getProgramId(), requestDTO.isCommonPlaylist())) {
//                response.setStatusCode(Constants.BAD_REQUEST);
//                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
//                response.setStatusMessage(Constants.PROGRAM_SCHEDULE_ALREADY_EXISTS);
//                return ResponseEntity.status(Constants.BAD_REQUEST).body(response);
//            }
            List<String> storeIds = requestDTO.getTerminalDetails().stream().map(TerminalDetails::getStoreId)  // assuming TerminalDetails has a getStoreId() method
                    .collect(Collectors.toList());
//        List<String> storeIds = Arrays.asList(requestDTO.getStoreIds());
            int storesActualCount = storeRepository.countByIdIn(storeIds);
            if (storesActualCount != storeIds.size()) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(Constants.STORE_IDs_MISSING);
                return ResponseEntity.status(Constants.BAD_REQUEST).body(response);
            }
            List<String> terminalLists = requestDTO.getTerminalDetails().stream().flatMap(terminalDetail -> Arrays.stream(terminalDetail.getTerminalList())) // assuming TerminalDetails has getTerminalList() method
                    .collect(Collectors.toList());

            int terminalActualCount = terminalRepository.countByIdIn(terminalLists);
            if (terminalActualCount != terminalLists.size()) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(Constants.TERMINAL_IDs_MISSING);
                return ResponseEntity.status(Constants.BAD_REQUEST).body(response);
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            ProgramScheduler programSchedule = new ProgramScheduler();
            Program program = new Program();
            program.setId(requestDTO.getProgramId());
            programSchedule.setProgram(program);
            programSchedule.setScheduleFrequency(requestDTO.getScheduleFrequency());
            programSchedule.setStartDate(formatter.parse(requestDTO.getStartDate()));
            programSchedule.setEndDate(formatter.parse(requestDTO.getEndDate()));
            programSchedule.setPlayStartTime(requestDTO.getPlayStartTime());
            programSchedule.setPlayEndTime(requestDTO.getPlayEndTime());
            programSchedule.setPlayMode(requestDTO.getPlayMode());
            programSchedule.setTimings(requestDTO.getTimings());
            programSchedule.setProgramOverlap(requestDTO.getProgramOverlap());
            programSchedule.setDownloadTime(requestDTO.getDownloadTime());
            programSchedule.setDownloadStartTime(requestDTO.getDownloadStartTime());
            programSchedule.setDownloadEndTime(requestDTO.getDownloadEndTime());
//            programSchedule.setCommonPlaylist(requestDTO.isCommonPlaylist());
            programSchedule.setCreatedBy(userId);
            programSchedule.setUpdatedBy(userId);

            ProgramScheduler programScheduleEntity = programScheduleRepository.save(programSchedule);

            List<ProgramSchedulerStoreDetails> programSchedulingStoreMappingList = new ArrayList<ProgramSchedulerStoreDetails>();

            for (String storeId : storeIds) {
                ProgramSchedulerStoreDetails schedulingStoreMapping = new ProgramSchedulerStoreDetails();

                schedulingStoreMapping.setProgramScheduler(new ProgramScheduler(programScheduleEntity.getId()));
                schedulingStoreMapping.setStores(new Stores(storeId));
                schedulingStoreMapping.setCreatedBy(userId);
                schedulingStoreMapping.setUpdatedBy(userId);
                programSchedulingStoreMappingList.add(schedulingStoreMapping);
            }

            List<ProgramSchedulerStoreDetails> programSchedulingStoreMappingEntities = programScheduleStoreMappingRepository.saveAll(programSchedulingStoreMappingList);

            List<ProgramSchedulerTerminalDetails> programSchedulingTerminalMappingList = new ArrayList<ProgramSchedulerTerminalDetails>();

            for (TerminalDetails terminalDetail : requestDTO.getTerminalDetails()) {
                // Find the corresponding store entity
                ProgramSchedulerStoreDetails storeDetails = programSchedulingStoreMappingEntities.stream().filter(storeMapping -> storeMapping.getStores().getId().equals(terminalDetail.getStoreId())).findFirst().orElse(null);

                if (storeDetails != null) {
                    // Iterate over terminalList for each store and save it
                    for (String terminalId : terminalDetail.getTerminalList()) {
                        ProgramSchedulerTerminalDetails programScheduleTerminalDetails = new ProgramSchedulerTerminalDetails();

                        programScheduleTerminalDetails.setProgramSchedulerStoreDetails(new ProgramSchedulerStoreDetails(storeDetails.getId()));
                        programScheduleTerminalDetails.setTerminal(new Terminal(terminalId));
                        programScheduleTerminalDetails.setCreatedBy(userId);
                        programScheduleTerminalDetails.setUpdatedBy(userId);
                        programSchedulingTerminalMappingList.add(programScheduleTerminalDetails);
                    }
                }
            }
            List<ProgramSchedulerTerminalDetails> programSchedulingTerminalMappingEntities = programScheduleTerminalMappingRepository.saveAll(programSchedulingTerminalMappingList);
            response.setStatusCode(Constants.CREATED);
            response.setStatus(Constants.CREATED_MESSAGE);
//            response.setStatusMessage((requestDTO.isCommonPlaylist())?Constants.COMMON_PLAYLIST_SCHEDULE_CREATED_SUCCESSFULLY:Constants.PROGRAM_SCHEDULE_CREATED_SUCCESSFULLY);
            return ResponseEntity.status(Constants.CREATED).body(response);
        } catch (ParseException parseException) {
            logger.error("ProgramServiceImpl:: createProgramScheduling:: Enter into the method...", parseException);
            parseException.printStackTrace();
            return null;
        } catch (Exception exception) {
            logger.error("ProgramServiceImpl:: createProgramScheduling:: Enter into the method...", exception);
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public ResponseEntity<?> getProgramSchedule(String id, String userId, String action) {
        try {
            CommonResponseDTO response = new CommonResponseDTO();
            List<Object[]> details;
            ProgramSchedulerDTO programSchedulerDTO;
            CommonResponseWithObjectDTO successResponse = new CommonResponseWithObjectDTO();
            switch (action){

                case Constants.PROGRAM :
                    Optional<Program> program = programRepository.findById(id);

                    if (!program.isPresent()) {
                        response.setStatusCode(Constants.BAD_REQUEST);
                        response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                        response.setStatusMessage(Constants.PROGRAM_NOT_FOUND);
                        return ResponseEntity.status(Constants.BAD_REQUEST).body(response);
                    }
                    details = programScheduleRepository.findProgramScheduleDetailsById(id);
                    programSchedulerDTO = mapper.mapToDTO(details);

                    successResponse.setStatusCode(Constants.OK);
                    successResponse.setStatus(Constants.OK_MESSAGE);
                    successResponse.setStatusMessage(Constants.PROGRAM_SCHEDULER_FETCHED_SUCCESSFULLY);
                    successResponse.setData(programSchedulerDTO);

                    return ResponseEntity.ok().body(successResponse);
                case Constants.COMMON_PLAYLIST:
                    Optional<CommonPlaylist> optionalCommonPlaylist = commonPlaylistRepository.findById(id);
                    if (!optionalCommonPlaylist.isPresent()) {
                        response.setStatusCode(Constants.BAD_REQUEST);
                        response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                        response.setStatusMessage(Constants.COMMON_PLAYLIST_NOT_FOUND);
                        return ResponseEntity.status(Constants.BAD_REQUEST).body(response);
                    }
                    details = commonPlaylistSchedulerRepository.findCommonPlaylistScheduleDetailsById(id);
                    programSchedulerDTO = mapper.mapToDTO(details);

                    successResponse.setStatusCode(Constants.OK);
                    successResponse.setStatus(Constants.OK_MESSAGE);
                    successResponse.setStatusMessage(Constants.COMMON_PLAYLIST_SCHEDULE_FETCHED_SUCCESSFULLY);
                    successResponse.setData(programSchedulerDTO);
                    return ResponseEntity.ok().body(successResponse);
                case Constants.POWER_PLAYLIST:
                    Optional<PowerPlaylist> powerPlaylistOptional = powerPlaylistRepository.findById(id);
                    if (!powerPlaylistOptional.isPresent()) {
                        response.setStatusCode(Constants.BAD_REQUEST);
                        response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                        response.setStatusMessage(Constants.COMMON_PLAYLIST_NOT_FOUND);
                        return ResponseEntity.status(Constants.BAD_REQUEST).body(response);
                    }
                    details = powerPlaylistSchedulerRepository.findPowerPlaylistScheduleDetailsById(id);
                    programSchedulerDTO = mapper.mapToDTO(details);

                    successResponse.setStatusCode(Constants.OK);
                    successResponse.setStatus(Constants.OK_MESSAGE);
                    successResponse.setStatusMessage(Constants.POWER_PLAYLIST_SCHEDULE_FETCHED_SUCCESSFULLY);
                    successResponse.setData(programSchedulerDTO);
                    return ResponseEntity.ok().body(successResponse);
                default:
                    response.setStatusCode(Constants.BAD_REQUEST);
                    response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                    response.setStatusMessage(Constants.INVALID_ACTION_TYPE);
                    return ResponseEntity.status(Constants.BAD_REQUEST).body(response);

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
