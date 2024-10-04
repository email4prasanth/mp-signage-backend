package com.module.idw_signage.controlller;

import com.module.idw_signage.service.CommonPlaylistService;
import com.module.idw_signage.service.ProgramServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/services")
public class CommonPlaylistController {

    @Autowired
    private CommonPlaylistService commonPlaylistService;

    @PutMapping("/commonplaylist/{programId}")
    public ResponseEntity<?> copyToCommonPlaylist(@RequestHeader("Authorization") String accessToken, @PathVariable String programId, HttpServletRequest request) {
        String userId =  request.getAttribute("userId").toString();
        String role = request.getAttribute("role").toString();
        return commonPlaylistService.copyProgramToCommonPlaylist(programId, userId,role);
    }

    @GetMapping("/commonplaylist/")
    public ResponseEntity<?> getCommonPlaylist(@RequestHeader("Authorization") String accessToken, HttpServletRequest request,
                                               @RequestParam("isCommonPlaylist") boolean isCommonPlaylist,
                                               @RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int limit,
                                               @RequestParam(required = false) String search) {
        String userId =  request.getAttribute("userId").toString();
        String role = request.getAttribute("role").toString();
        return commonPlaylistService.getCommonPlaylist(role,isCommonPlaylist,page,limit,search);
    }
}
