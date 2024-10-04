package com.module.idw_signage.service;

import com.module.idw_signage.Utils.Constants;
import com.module.idw_signage.dto.CommonResponseDTO;
import com.module.idw_signage.dto.CommonResponseWithObjectDTO;
import com.module.idw_signage.dto.MediaDTO;
import com.module.idw_signage.model.Media;
import com.module.idw_signage.model.Stores;
import com.module.idw_signage.repository.MediaRepository;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MediaServiceImpl implements MediaService {

    private static final Logger logger = LoggerFactory.getLogger(MediaServiceImpl.class);

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private StoreRepository storeRepository;

    public ResponseEntity<?> createMedia(MediaDTO mediaDTO, HttpServletRequest request) {

            String userId = request.getAttribute("userId").toString();
            Media media = new Media();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            Stores store = storeRepository.findById(mediaDTO.getStoreID())
                    .orElseThrow(() -> new RuntimeException("Store not found"));

            CommonResponseDTO response = new CommonResponseDTO();

            // Validate all fields
            if (mediaDTO.getFileName() == null || mediaDTO.getFileName().isEmpty()) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(Constants.MSG_INVALID_FILE_NAME);
                return ResponseEntity.badRequest().body(response);
            } else if (mediaDTO.getSize() == null || mediaDTO.getSize() <= 0) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(Constants.MSG_SIZE_MUST_BE_GREATER_THAN_ZERO);
                return ResponseEntity.badRequest().body(response);
            } else if (mediaDTO.getDuration() == null || mediaDTO.getDuration().isEmpty()) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(Constants.MSG_DURATION_CANNOT_BE_NULL_OR_EMPTY);
                return ResponseEntity.badRequest().body(response);
            } else if (mediaDTO.getUploader() == null || mediaDTO.getUploader().isEmpty()) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(Constants.MSG_UPLOADER_CANNOT_BE_NULL_OR_EMPTY);
                return ResponseEntity.badRequest().body(response);
            } else if (mediaDTO.getLink() == null || mediaDTO.getLink().isEmpty()) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(Constants.MSG_LINK_CANNOT_BE_EMPTY);
                return ResponseEntity.badRequest().body(response);
            } else if (mediaDTO.getChecker() != null && mediaDTO.getChecker().isEmpty()) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(Constants.MSG_CHECKER_CANNOT_BE_EMPTY);
                return ResponseEntity.badRequest().body(response);
            } else if (mediaDTO.getStatus() == null || mediaDTO.getStatus().isEmpty()) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(Constants.MSG_STATUS_CANNOT_BE_NULL_OR_EMPTY);
                return ResponseEntity.badRequest().body(response);
            } else {
                // Check for existing media with the same file name and store
                Optional<Media> existingMedia = mediaRepository.findByFileNameAndStores(mediaDTO.getFileName(), store);
                if (existingMedia.isPresent()) {
                    response.setStatusCode(Constants.BAD_REQUEST);
                    response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                    response.setStatusMessage(Constants.MSG_MEDIA_ALREADY_EXISTS);
                    return ResponseEntity.badRequest().body(response);
                } else {
                    media.setStores(store);
                    media.setId(UUID.randomUUID().toString());
                    media.setFileName(mediaDTO.getFileName());
                    media.setSize(mediaDTO.getSize());
                    media.setDuration(mediaDTO.getDuration());
                    media.setUploader(mediaDTO.getUploader());

                    Date expiryDate;
                    try {
                        expiryDate = formatter.parse(mediaDTO.getExpiryDate());
                    } catch (ParseException e) {
                        response.setStatusCode(Constants.INTERNAL_SERVER_ERROR);
                        response.setStatus(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
                        response.setStatusMessage(Constants.EXCEPTION_MADE);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                    }

                    media.setExpiryDate(expiryDate);
                    media.setLink(mediaDTO.getLink());
                    media.setChecker(mediaDTO.getChecker());
                    media.setStatus(mediaDTO.getStatus());
                    media.setCreatedBy(userId);
                    mediaRepository.save(media);

                    response.setStatusCode(Constants.CREATED);
                    response.setStatus(Constants.CREATED_MESSAGE);
                    response.setStatusMessage(Constants.MSG_CREATED_SUCCESSFULLY);
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                }
            }
        }



        public ResponseEntity<?> getMedia(String storeId,int page, int limit, String search) {
            logger.info("HandlerInterceptorImpl:: getStore :: Entered into getMedia method..");
            CommonResponseWithObjectDTO responseDTO = new CommonResponseWithObjectDTO();
           List<Media> mediaList = mediaRepository.findByStoresId(storeId);
try{

            Sort sort = Sort.by(Sort.Order.desc("createdDate"));
            if (page <= 0) {
                page = 1;
            }
            if (limit <= 0) {
                page = 1;
            }
            PageRequest pageRequest = PageRequest.of(page - 1, limit, sort);
            Page<Media> mediaPage;

            if (search != null && !search.trim().isEmpty()) {
                mediaPage = mediaRepository.findByStoresIdAndFileNameContainingOrStoresIdAndLinkContaining(storeId, search, storeId, search, pageRequest);
            } else {
                mediaPage = mediaRepository.findByStoresId(storeId, pageRequest);
            }



            List<MediaDTO> mediaDTOS = mediaPage.stream().map(media -> {
                MediaDTO mediaDTO = new MediaDTO();
                mediaDTO.setMediaId(media.getId());
                mediaDTO.setStoreID(media.getStores().getId());
                mediaDTO.setFileName(media.getFileName());
                mediaDTO.setSize(media.getSize());
                mediaDTO.setDuration(media.getDuration());
                mediaDTO.setUploader(media.getUploader());
                mediaDTO.setExpiryDate(String.valueOf(media.getExpiryDate()));
                mediaDTO.setLink(media.getLink());
                mediaDTO.setChecker(media.getChecker());
                mediaDTO.setStatus(media.getStatus());
                return mediaDTO;
            }).toList();

            if (mediaDTOS.isEmpty()) {
                responseDTO.setStatusCode(Constants.BAD_REQUEST);
                responseDTO.setStatus("Bad Request");
                responseDTO.setStatusMessage(Constants.MSG_MEDIA_NOT_FOUND);
                responseDTO.setData(Collections.emptyList());// Return an empty list in the data field
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
            }

            responseDTO.setStatusCode(Constants.OK);
            responseDTO.setStatus("OK");
            responseDTO.setStatusMessage(Constants.MEDIA_FETCHED_SUCCESSFULLY);
            responseDTO.setData(mediaDTOS);
            responseDTO.setTotalPages(mediaPage.getTotalPages());
            responseDTO.setTotalRecords((long)mediaPage.getTotalElements());

            return ResponseEntity.ok(responseDTO);

        }
catch (Exception e) {

      responseDTO.setStatusCode(Constants.BAD_REQUEST);
        responseDTO.setStatus("Bad Request");
        responseDTO.setStatusMessage(Constants.MSG_MEDIA_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
}
   }
}