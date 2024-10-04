package com.module.idw_signage.service;

import com.module.idw_signage.dto.PlaylistDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface PlaylistService {

    public ResponseEntity<?> createPlaylist (@RequestBody PlaylistDTO playlistDTO, HttpServletRequest request);
    public ResponseEntity<?> getPlaylists (String storeId, int page, int limit, String search);

}
