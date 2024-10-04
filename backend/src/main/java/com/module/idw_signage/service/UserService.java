package com.module.idw_signage.service;

import com.module.idw_signage.dto.UserRequestDTO;
import org.springframework.http.ResponseEntity;

public interface UserService {

//    public ResponseEntity<?> createUser(String storeName, String storeCatetory, String storeLocation, String userId, String role);

    public ResponseEntity<?> getUsers(int page, int limit, String search, String roleName, String loggedInUser);

    public ResponseEntity<?> deleteUser(String id);

    ResponseEntity<?> createUser(String fullName, String email, String access,String password, String[] stores, String userId);

    ResponseEntity<?> editUser(UserRequestDTO requestDTO, String updatingUserId);
}
