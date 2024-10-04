package com.module.idw_signage.controlller;


import com.module.idw_signage.dto.UserRequestDTO;
import com.module.idw_signage.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/services/user/")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getUsers(@RequestHeader("Authorization") String accessToken, HttpServletRequest request,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int limit,
                                      @RequestParam(required = false) String search) {
        logger.info("UserController:: getUsers:: Enter into the method...");

        String loggedInUser =  request.getAttribute("userId").toString();
        String roleName = request.getAttribute("role").toString();
        ResponseEntity<?> response = userService.getUsers( page, limit,search, roleName, loggedInUser);

        return response;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestHeader("Authorization") String accessToken, @RequestBody UserRequestDTO requstDTO, HttpServletRequest request) {
        logger.info("UserController:: getUsers:: Enter into the method...");

        String userId =  request.getAttribute("userId").toString();
        String role = request.getAttribute("role").toString();
        ResponseEntity<?> response = userService.createUser(requstDTO.getFullname(), requstDTO.getEmail(), requstDTO.getAccess(),requstDTO.getPassword() , requstDTO.getStores(), userId);

        return response;
    }
    @PutMapping
    public ResponseEntity<?> editUser(@RequestHeader("Authorization") String accessToken, @RequestBody UserRequestDTO requestDTO , HttpServletRequest request) {
        logger.info("UserController:: getUsers:: Enter into the method...");

        String updatingUserId =  request.getAttribute("userId").toString();
        String role = request.getAttribute("role").toString();
        ResponseEntity<?> response = userService.editUser(requestDTO,updatingUserId);

        return response;
    }
    @DeleteMapping("{id}")
    public  ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String accessToken,@PathVariable String id){
        ResponseEntity<?> response = userService.deleteUser(id);
        return response;

    }


}
