package com.module.idw_signage.controlller;

import com.module.idw_signage.dto.MediaDTO;
import com.module.idw_signage.service.MediaServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/services")
public class MediaController {
    private static final Logger logger = LoggerFactory.getLogger(MediaController.class);


    @Autowired
    private MediaServiceImpl mediaService;

    @PostMapping("/media")
    public ResponseEntity<?> createMedia(@RequestHeader("Authorization") String accessToken,@RequestBody MediaDTO mediaDTO, HttpServletRequest request){
        return mediaService.createMedia(mediaDTO,request);
    }


    @GetMapping("/media/{storeID}")
    public ResponseEntity<?>  getMedia (@RequestHeader("Authorization") String accessToken,@PathVariable("storeID")  String storeID,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int limit,
    @RequestParam(required = false) String search) {
        logger.info("MediaController:: getMedia:: Enter into the method...");

        return  mediaService.getMedia(storeID,page, limit, search);

    }

}
