package com.module.idw_signage.controlller;

import com.module.idw_signage.dto.PlaylistDTO;
import com.module.idw_signage.service.PlaylistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/services")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @PostMapping("/playlist")
    public ResponseEntity<?> createPlaylist(@RequestHeader("Authorization") String accessToken,@RequestBody PlaylistDTO playlistDTO, HttpServletRequest request) {
   return playlistService.createPlaylist(playlistDTO, request);
    }

    @GetMapping("/playlist/{storeID}")
    public ResponseEntity<?> getPlaylists (@RequestHeader("Authorization") String accessToken, @PathVariable("storeID")  String storeID,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search) {
        return playlistService.getPlaylists(storeID, page, limit, search);
    }

}
