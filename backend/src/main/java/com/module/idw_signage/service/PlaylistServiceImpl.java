package com.module.idw_signage.service;

import com.module.idw_signage.dto.CommonResponseDTO;
import com.module.idw_signage.dto.CommonResponseWithObjectDTO;
import com.module.idw_signage.dto.PlaylistDTO;
import com.module.idw_signage.model.PowerPlaylist;
import com.module.idw_signage.model.Stores;
import com.module.idw_signage.repository.PowerPlaylistRepository;
import com.module.idw_signage.repository.StoreRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private static final Logger logger = LoggerFactory.getLogger(PlaylistServiceImpl.class);

    @Autowired
    private PowerPlaylistRepository playlistRepository;

    @Autowired
    private StoreRepository storeRepository;


    public ResponseEntity<?> createPlaylist(@RequestBody PlaylistDTO playlistDTO, HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        PowerPlaylist powerPlaylist = new PowerPlaylist();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");


        Stores store = storeRepository.findById(playlistDTO.getStoreID())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        CommonResponseDTO response = new CommonResponseDTO();

        if (playlistDTO.getProgramName() == null || playlistDTO.getProgramName().isEmpty()) {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setStatus("Bad Request");
            response.setStatusMessage("Program name cannot be null or empty");
            return ResponseEntity.badRequest().body(response);
        } else if (playlistDTO.getDuration() == null || playlistDTO.getDuration().isEmpty()) {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setStatus("Bad Request");
            response.setStatusMessage("Duration cannot be null or empty");
            return ResponseEntity.badRequest().body(response);
        } else if (playlistDTO.getSize() == null || playlistDTO.getSize().isEmpty()) {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setStatus("Bad Request");
            response.setStatusMessage("Size cannot be null or empty");
            return ResponseEntity.badRequest().body(response);
        } else if (playlistDTO.getStartDate() == null || playlistDTO.getEndDate() == null) {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setStatus("Bad Request");
            response.setStatusMessage("Start date and end date cannot be null");
            return ResponseEntity.badRequest().body(response);
        } else {
            // Check for existing playlist with the same program name and store
            Optional<PowerPlaylist> existingPlaylist = playlistRepository.findByProgramNameAndStore(playlistDTO.getProgramName(), store);
            if (existingPlaylist.isPresent()) {
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                response.setStatus("Bad Request");
                response.setStatusMessage("Playlist with the same program name already exists");
                return ResponseEntity.badRequest().body(response);
            } else {
                powerPlaylist.setStore(store);
                powerPlaylist.setId(UUID.randomUUID().toString());
                powerPlaylist.setProgramName(playlistDTO.getProgramName());
                powerPlaylist.setDuration(playlistDTO.getDuration());
                powerPlaylist.setSize(playlistDTO.getSize());

                Date startDate;
                Date endDate;
                try {
                    startDate = format.parse(playlistDTO.getStartDate());
                    endDate = format.parse(playlistDTO.getEndDate());
                } catch (ParseException e) {
                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    response.setStatus("Internal Server Error");
                    response.setStatusMessage("Exception occurred while parsing dates");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
                powerPlaylist.setStartDate(startDate);
                powerPlaylist.setEndDate(endDate);
                powerPlaylist.setPrior(playlistDTO.getPrior());
                powerPlaylist.setReview(playlistDTO.getReview());
                powerPlaylist.setPlayFrequency(playlistDTO.getPlayFrequency());
                powerPlaylist.setCreatedBy(userId);
                playlistRepository.save(powerPlaylist);

                response.setStatusCode(HttpStatus.CREATED.value());
                response.setStatus("Created");
                response.setStatusMessage("Playlist created successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }

    }
    }


    public ResponseEntity<?> getPlaylists (String storeId, int page, int limit, String search) {
        logger.info("Entering getPlaylists method...");
        CommonResponseWithObjectDTO responseDTO = new CommonResponseWithObjectDTO();

        try {
            Sort sort = Sort.by(Sort.Order.desc("createdAt"));
            if (page <= 0) {
                page = 1;
            }
            if (limit <= 0) {
                limit = 10; // Set default limit
            }
            PageRequest pageRequest = PageRequest.of(page - 1, limit, sort);
            Page<PowerPlaylist> playlistPage;

            if (search != null && !search.trim().isEmpty()) {
                playlistPage = playlistRepository.findByStoreIdAndProgramNameContainingOrStoreIdAndReviewContaining(
                        storeId, search, storeId, search, pageRequest);
            } else {
                playlistPage = playlistRepository.findByStoreId(storeId, pageRequest);
            }

            List<PlaylistDTO> playlistDTOS = playlistPage.stream().map(powerPlaylist -> {
                PlaylistDTO playlistDTO = new PlaylistDTO();
                playlistDTO.setId(powerPlaylist.getId());
                playlistDTO.setStoreID(powerPlaylist.getStore().getId());
                playlistDTO.setProgramName(powerPlaylist.getProgramName());
                playlistDTO.setDuration(powerPlaylist.getDuration());
                playlistDTO.setSize(powerPlaylist.getSize());
                playlistDTO.setStartDate(powerPlaylist.getStartDate().toString());
                playlistDTO.setEndDate(powerPlaylist.getEndDate().toString());
                playlistDTO.setPrior(powerPlaylist.getPrior());
                playlistDTO.setReview(powerPlaylist.getReview());
                playlistDTO.setPlayFrequency(powerPlaylist.getPlayFrequency());
                return playlistDTO;
            }).toList();

            if (playlistDTOS.isEmpty()) {
                responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
                responseDTO.setStatus("Bad Request");
                responseDTO.setStatusMessage("No playlists found");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
            }

            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setStatus("OK");
            responseDTO.setStatusMessage("Playlists fetched successfully");
            responseDTO.setData(playlistDTOS);
            responseDTO.setTotalRecords(playlistPage.getTotalElements());
            responseDTO.setTotalPages(playlistPage.getTotalPages());
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setStatus("Bad Request");
            responseDTO.setStatusMessage("An error occurred while fetching playlists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }
    }


            }


