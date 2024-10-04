package com.module.idw_signage.service;


import org.springframework.http.ResponseEntity;

public interface CommonPlaylistService {

    public ResponseEntity<?> copyProgramToCommonPlaylist(String programId, String userId, String role);

    ResponseEntity<?> getCommonPlaylist(String role, boolean isCommonPlaylist,int page, int limit,String search);
}
