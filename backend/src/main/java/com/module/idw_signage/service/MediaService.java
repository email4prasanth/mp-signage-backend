package com.module.idw_signage.service;

import com.module.idw_signage.dto.MediaDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface MediaService {
    public ResponseEntity<?> createMedia (MediaDTO mediaDTO, HttpServletRequest request);

    public ResponseEntity<?>  getMedia(String storeId,int page, int limit, String search);
}

