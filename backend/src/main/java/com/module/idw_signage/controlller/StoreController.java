package com.module.idw_signage.controlller;
/*
    Created At 05/09/2024
    Author @Hubino

 */
import com.module.idw_signage.dto.StoreDTO;
import com.module.idw_signage.dto.TestDTO;
import com.module.idw_signage.service.StoreService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/services")
public class StoreController {
    private static final Logger logger = LoggerFactory.getLogger(StoreController.class);

    @Autowired(required=true)
    private StoreService storeService;

    @GetMapping("/test")
    public ResponseEntity<TestDTO> interceptorTest(@RequestHeader("Authorization") String accessToken,
                                             @RequestParam("value") String value ) {
        logger.info("MainController:: interceptorTest:: Enter into the method...");
        TestDTO testDTO = new TestDTO();
        testDTO.setValues(value);
    return ResponseEntity.ok(testDTO);
    }

    @PostMapping("/store")
    public ResponseEntity<?> saveStore(@RequestHeader("Authorization") String accessToken, @RequestBody StoreDTO storeDTO, HttpServletRequest request) {
        logger.info("MainController:: saveStore:: Enter into the method...");
        ResponseEntity<?> response = storeService.createStore(storeDTO.getStoreName(), storeDTO.getStoreCategory(), storeDTO.getStoreLocation(), request.getAttribute("userId").toString(), request.getAttribute("role").toString());

        return response;
    }

    @GetMapping("/store")
    public ResponseEntity<?> getStore(@RequestHeader("Authorization") String accessToken, HttpServletRequest request,
     @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int limit,
    @RequestParam(required = false) String search ) {
        logger.info("MainController:: getStore:: Enter into the method...");

        String userId =  request.getAttribute("userId").toString();
        String role = request.getAttribute("role").toString();

        ResponseEntity<?> response = storeService.getStore(userId, role, page, limit, search);

        return response;
    }


}
