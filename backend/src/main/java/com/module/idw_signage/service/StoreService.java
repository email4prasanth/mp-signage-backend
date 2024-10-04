package com.module.idw_signage.service;

import org.springframework.http.ResponseEntity;

public interface StoreService {

    public ResponseEntity<?> createStore(String storeName, String storeCatetory, String storeLocation, String userId, String role);

    public ResponseEntity<?> getStore(String userId, String role,int page, int limit, String search);
}
