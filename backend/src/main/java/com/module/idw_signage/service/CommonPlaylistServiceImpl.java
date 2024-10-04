package com.module.idw_signage.service;

import com.module.idw_signage.Utils.Constants;
import com.module.idw_signage.dto.CommonPlaylistDTO;
import com.module.idw_signage.dto.CommonResponseDTO;
import com.module.idw_signage.dto.CommonResponseWithObjectDTO;
import com.module.idw_signage.model.CommonPlaylist;
import com.module.idw_signage.model.Program;
import com.module.idw_signage.repository.CommonPlaylistRepository;
import com.module.idw_signage.repository.ProgramRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommonPlaylistServiceImpl implements CommonPlaylistService {

    private static final Logger logger = LoggerFactory.getLogger(MediaServiceImpl.class);

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private CommonPlaylistRepository commonPlaylistRepository;

    @Override
    public ResponseEntity<?> copyProgramToCommonPlaylist(String programId, String userId, String role) {


        logger.info("ProgramServiceImpl:: copyProgramToCommonPlaylist:: Enter into the method...");

        CommonResponseDTO response = new CommonResponseDTO();

        try {
            // Check if the program exists
            Optional<Program> optionalProgram = programRepository.findById(programId);
            if (optionalProgram.isEmpty()) {

                response.setStatusCode(Constants.NOT_FOUND);
                response.setStatus(Constants.NOT_FOUND_MESSAGE);
                response.setStatusMessage(Constants.PROGRAM_NOT_FOUND);
                return ResponseEntity.status(Constants.NOT_FOUND).body(response);
            }

            Program program = optionalProgram.get();

            // Check if the program already exists in the common playlist
            List<CommonPlaylist> exists = commonPlaylistRepository.findByProgramId(programId);
            if (!exists.isEmpty()) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(Constants.PROGRAM_ALREADY_IN_COMMON_PLAYLIST);
                return ResponseEntity.status(Constants.BAD_REQUEST).body(response);
            }


            // Create a new CommonPlaylist entry
            CommonPlaylist commonPlaylist = new CommonPlaylist();

            commonPlaylist.setId(UUID.randomUUID().toString()); // Generate a unique ID
            commonPlaylist.setUserId(userId);
            commonPlaylist.setProgram(program);
            commonPlaylist.setProgramName(program.getProgramName());
            commonPlaylist.setStatus(program.getStatus());
            commonPlaylist.setLink(program.getLink());
            commonPlaylist.setFileName(program.getFileName());
            commonPlaylist.setExpiryDate(program.getExpiryDate());
            commonPlaylist.setFormat(program.getFormat());
            commonPlaylist.setDuration(program.getDuration());
            commonPlaylist.setFileSize(program.getFileSize());
            commonPlaylist.setRole(role);
            commonPlaylist.setCreatedBy(userId);
            commonPlaylist.setUpdatedBy(userId);


            // Save the common playlist entry
            commonPlaylistRepository.save(commonPlaylist);


            response.setStatusCode(Constants.CREATED);
            response.setStatus(Constants.CREATED_MESSAGE);
            response.setStatusMessage(Constants.PROGRAM_COPIED_TO_COMMON_PLAYLIST_SUCCESSFULLY);
            return ResponseEntity.status(Constants.CREATED).body(response);

        } catch (Exception e) {
            logger.error("ProgramServiceImpl:: copyProgramToCommonPlaylist:: Exception occurred", e);
            response.setStatusCode(Constants.INTERNAL_SERVER_ERROR);
            response.setStatus(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
            response.setStatusMessage(Constants.EXCEPTION_MADE);
            return ResponseEntity.status(Constants.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<?> getCommonPlaylist(String role, boolean isCommonPlaylist, int page, int limit, String search) {
        CommonResponseWithObjectDTO responseDTO = new CommonResponseWithObjectDTO();

        try {
            Sort sort = Sort.by(Sort.Order.desc("id"));
            if (page <= 0) {
                page = 1;
            }
            if (limit <= 0) {
                limit = 10;
            }
            PageRequest pageRequest = PageRequest.of(page - 1, limit, sort);
            Page<CommonPlaylist> commonPlaylistPage;
            if (!role.equalsIgnoreCase("Store Admin") && isCommonPlaylist) {
                if (search != null && !search.trim().isEmpty()) {
                    commonPlaylistPage = commonPlaylistRepository.findByProgramNameContaining(search, pageRequest);
                } else {
                    commonPlaylistPage = commonPlaylistRepository.findAll(pageRequest);
                }
                List<CommonPlaylistDTO> commonPlaylistDTOS = commonPlaylistPage.stream().map(commonPlaylist -> {
                    CommonPlaylistDTO commonPlaylistDTO = new CommonPlaylistDTO();
                    commonPlaylistDTO.setId(commonPlaylist.getId());
                    commonPlaylistDTO.setProgramId(commonPlaylist.getProgram().getId());
                    commonPlaylistDTO.setProgramName(commonPlaylist.getProgramName());
                    commonPlaylistDTO.setStatus(commonPlaylist.getStatus());
                    commonPlaylistDTO.setRole(commonPlaylist.getRole());
                    return commonPlaylistDTO;
                }).collect(Collectors.toList());

                if (commonPlaylistDTOS.isEmpty()) {
                    CommonResponseDTO errorResponse = new CommonResponseDTO();
                    errorResponse.setStatusCode(Constants.NOT_FOUND);
                    errorResponse.setStatus(Constants.NOT_FOUND_MESSAGE);
                    errorResponse.setStatusMessage(Constants.NO_COMMON_PLAYLIST);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                }
                responseDTO.setStatusCode(Constants.OK);
                responseDTO.setStatus(Constants.OK_MESSAGE);
                responseDTO.setStatusMessage(Constants.COMMON_PLAYLIST_FETCHED);
                responseDTO.setData(commonPlaylistDTOS);
                responseDTO.setTotalRecords(commonPlaylistPage.getTotalElements());
                responseDTO.setTotalPages(commonPlaylistPage.getTotalPages());
                return ResponseEntity.ok(responseDTO);
            } else {
                CommonResponseDTO errorResponse = new CommonResponseDTO();
                errorResponse.setStatusCode(Constants.UNAUTHORIZED);
                errorResponse.setStatus(Constants.UNAUTHORIZED_MESSAGE);
                errorResponse.setStatusMessage(Constants.UNAUTHORIZED_USER);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
        } catch (Exception e) {
            CommonResponseDTO response = new CommonResponseDTO();
            logger.error("ProgramServiceImpl:: copyProgramToCommonPlaylist:: Exception occurred", e);
            response.setStatusCode(Constants.INTERNAL_SERVER_ERROR);
            response.setStatus(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
            response.setStatusMessage(Constants.EXCEPTION_MADE);
            return ResponseEntity.status(Constants.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}

