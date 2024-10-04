package com.module.idw_signage.service;
/*
    Created At 05/09/2024
    Author @Hubino
 */
import com.module.idw_signage.Utils.Constants;
import com.module.idw_signage.dto.CommonResponseDTO;
import com.module.idw_signage.dto.CommonResponseWithObjectDTO;
import com.module.idw_signage.dto.StoreResponseDTO;
import com.module.idw_signage.model.Stores;
import com.module.idw_signage.model.Users;
import com.module.idw_signage.repository.StoreRepository;
import com.module.idw_signage.validation.StoreValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class StoreServiceImpl implements StoreService {

    private static final Logger logger = LoggerFactory.getLogger(StoreServiceImpl.class);
    @Autowired
    private StoreValidation storeValidation;

    @Autowired
    private StoreRepository storeRepository;

    @Override
    public ResponseEntity<?> createStore(String storeName, String storeCategory, String storeLocation, String userId, String role) {
        logger.info("HandlerInterceptorImpl:: createStore :: Entered into getStore method..");
        try {
            String validation = storeValidation.saveStoreValidation(storeName,storeCategory, storeLocation);
            CommonResponseDTO response = new CommonResponseDTO();
            if (validation != null) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(validation);
                return ResponseEntity.status(400).body(response);
            }
            Stores storesEntity = new Stores();
            storesEntity.setId(UUID.randomUUID().toString());
            Users users = new Users();
            users.setId(userId);
            storesEntity.setUser(users);
            storesEntity.setStoreName(storeName);
            storesEntity.setStoreCategory(storeCategory);
            storesEntity.setStoreLocation(storeLocation);
            storesEntity.setCreatedBy(userId);
            storesEntity.setUpdatedBy(userId);

            Stores storesResponse = storeRepository.save(storesEntity);
            if (storesResponse.getId() != null) {
                response.setStatusCode(Constants.CREATED);
                response.setStatus(Constants.CREATED_MESSAGE);
                response.setStatusMessage(Constants.STORE_CREATED_SUCCESSFULLY);
                return ResponseEntity.status(Constants.CREATED).body(response);
            } else {
                response.setStatusCode(Constants.INTERNAL_SERVER_ERROR);
                response.setStatus(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
                response.setStatusMessage(Constants.EXCEPTION_MADE);
                return ResponseEntity.status(Constants.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            logger.error("HandlerInterceptorImpl:: createStore :: Made an exception: "+e);
            return null;
        }
    }

    @Override
    public ResponseEntity<?> getStore(String userId, String role, int page, int limit, String search) {
        logger.info("HandlerInterceptorImpl:: getStore :: Entered into getStore method..");
        List<Stores> storesList;
        try {
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
            if (page <= 0) {
                page = 1;}

            if (limit <= 0) {
                limit = 10;}

            PageRequest pageRequest = PageRequest.of(page - 1, limit, sort);
            Page<Stores> storesPage;

        if(role.equals(Constants.ADMIN)) {
            logger.info("HandlerInterceptorImpl:: getStore :: Entered if condition..");

            if (search != null && !search.trim().isEmpty()) {
                storesPage = storeRepository.findByStoreNameContainingOrStoreLocationContaining(search, search, pageRequest);

            } else {
                storesPage = storeRepository.findAll(pageRequest);
            }
        }else {
            logger.info("HandlerInterceptorImpl:: getStore :: Entered into else condition..");
            if (search != null && !search.trim().isEmpty()) {
                storesPage = storeRepository.findByUserIdAndStoreNameContainingOrStoreLocationContaining(userId, search, search, pageRequest);
            } else {
                storesPage = storeRepository.findByUserId(userId, pageRequest);
            }
        }

        List<StoreResponseDTO> storeResponseDTOS = storesPage.getContent().stream().map(store -> {
                    StoreResponseDTO dto = new StoreResponseDTO();
                    dto.setStoreId(store.getId());
                    dto.setStoreName(store.getStoreName());
                    dto.setStoreLocation(store.getStoreLocation());
                    return dto;
                })
                .collect(Collectors.toList());

            int totalStores;
            if (role.equals(Constants.ADMIN)) {
                if (search != null && !search.trim().isEmpty()) {
                    totalStores = storeRepository.countByStoreNameContainingOrStoreLocationContaining(search, search);
                } else {
                    totalStores = (int) storeRepository.count();
                }
            } else {
                if (search != null && !search.trim().isEmpty()) {
                    totalStores = storeRepository.countByUserIdAndStoreNameContainingOrStoreLocationContaining(userId, search, search);
                } else {
                    totalStores = storeRepository.countByUserId(userId);
                }
            }


        CommonResponseWithObjectDTO responseDTO = new CommonResponseWithObjectDTO();
            responseDTO.setStatusCode(Constants.OK);
            responseDTO.setStatus(Constants.OK_MESSAGE);
            responseDTO.setStatusMessage(Constants.STORES_FETCHED_SUCCESSFUL);
            responseDTO.setData(storeResponseDTOS);

            responseDTO.setTotalPages(storesPage.getTotalPages());
            responseDTO.setTotalRecords(totalStores);

            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("HandlerInterceptorImpl:: getStore :: Made an exception: "+e.toString());
            CommonResponseWithObjectDTO errorResponse = new CommonResponseWithObjectDTO();
            errorResponse.setStatusCode(Constants.INTERNAL_SERVER_ERROR);
            errorResponse.setStatus("Internal Server Error");
            errorResponse.setStatusMessage("An exception occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }


    }
}
