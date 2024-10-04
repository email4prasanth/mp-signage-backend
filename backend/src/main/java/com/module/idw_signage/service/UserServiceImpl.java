package com.module.idw_signage.service;

import com.module.idw_signage.Utils.Constants;
import com.module.idw_signage.dto.*;
import com.module.idw_signage.model.StoreMapping;
import com.module.idw_signage.model.Stores;
import com.module.idw_signage.model.Users;
import com.module.idw_signage.repository.StoreMappingRepository;
import com.module.idw_signage.repository.UsersRepository;
import com.module.idw_signage.validation.UsersValidation;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class UserServiceImpl implements  UserService{

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UsersValidation usersValidation;

    @Autowired
    private StoreMappingRepository storeMappingRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public ResponseEntity<?> getUsers(int page, int limit, String search, String roleName, String loggedInUserId) {
        logger.info("UserServiceImpl:: getUsers :: Entered into the method..");

        // Sort sort = Sort.by(Sort.Order.desc("createdAt"))
        //List<Users> usersList = usersRepository.findAll(sort);
        CommonResponseDTO response = new CommonResponseDTO();
        try {

            if (roleName.equals(Constants.STORE_ADMIN)) {
                response.setStatusCode(Constants.UNAUTHORIZED);
                response.setStatus(Constants.UNAUTHORIZED_MESSAGE);
                response.setStatusMessage(Constants.UNAUTHORIZED_USER);
                return ResponseEntity.status(Constants.UNAUTHORIZED).body(response);
            }
            Sort sort = Sort.by(Sort.Order.desc("createdAt"));
            if (page <= 0) {
                page = 1;
            }
            if (limit <= 0) {
                limit = 10;
            }
            PageRequest pageRequest = PageRequest.of(page - 1, limit, sort);
            Page<Users> usersPage = null;
            List<UsersResponseDTO> usersResponseDTOS;
            String searchField = (search == null || search.trim().isEmpty()) ? "" : search;
            int totalCount = 0;
            if (roleName.equals(Constants.SUPER_ADMIN)) {
                logger.info("UserServiceImpl:: getUsers :: Entered into super admin request part..");
                usersPage = usersRepository.findByUsernameContainingOrFullnameContainingOrEmailContaining(searchField, searchField, searchField, pageRequest);
                usersResponseDTOS = usersPage.stream().map(user -> mapToDTO(user)).collect(Collectors.toList());
            } else {
                logger.info("UserServiceImpl:: getUsers :: Entered into other user roles part..");
//                System.out.println("(page-1 ) * limit: " + (page - 1) * limit + " limit: " + limit+ "searchField: "+searchField+ "loggedInUserId:  "+loggedInUserId);
                List<Object[]> usersObjects = usersRepository.customFindUsersWithRecursiveQuery(loggedInUserId, searchField, (page - 1) * limit, limit);
                if (usersObjects.isEmpty()) {
                    response.setStatusCode(Constants.NOT_FOUND);
                    response.setStatus(Constants.NOT_FOUND_MESSAGE);
                    response.setStatusMessage(Constants.USER_NOT_FOUND);
                    return ResponseEntity.status(Constants.BAD_REQUEST).body(response);
                }
                Object[] firstRow = usersObjects.get(0); // Get the first row
                totalCount = ((Number) firstRow[firstRow.length - 1]).intValue(); // Assuming the last column contains the count
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                usersResponseDTOS = usersObjects.stream().map(obj -> {
                    UsersResponseDTO dto = new UsersResponseDTO();
                    dto.setUserId((String) obj[0]);
                    dto.setUsername((String) obj[1]);
                    dto.setEmail((String) obj[3]);
                    dto.setAccess((String) obj[5]); // Assuming this is the role
                    Timestamp lastLoginTimestamp = (Timestamp) obj[4];
                    if (lastLoginTimestamp != null) {
                        dto.setLastLogin(lastLoginTimestamp.toLocalDateTime().format(formatter));
                    }
                    Timestamp dateAddedTimestamp = (Timestamp) obj[6];
                    if (dateAddedTimestamp != null) {
                        dto.setDateAdded(dateAddedTimestamp.toLocalDateTime().format(formatter));
                    }
                    return dto;
                }).collect(Collectors.toList());
            }

            CommonResponseWithObjectDTO responseDTO = new CommonResponseWithObjectDTO();
            responseDTO.setStatusCode(Constants.OK);
            responseDTO.setStatus(Constants.OK_MESSAGE);
            responseDTO.setStatusMessage(Constants.USERS_FETCHED_SUCCESSFUL);
            responseDTO.setData(usersResponseDTOS);
            responseDTO.setTotalPages((usersPage == null) ? totalCount / limit +1 : usersPage.getTotalPages());
            responseDTO.setTotalRecords((usersPage == null) ? totalCount : usersPage.getTotalElements());

            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            logger.error("UserServiceImpl:: getUsers :: Exception made.."+ e);
            e.printStackTrace();
            response.setStatusCode(Constants.INTERNAL_SERVER_ERROR);
            response.setStatus(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
            response.setStatusMessage(Constants.EXCEPTION_MADE);
            return ResponseEntity.status(Constants.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    private UsersResponseDTO mapToDTO(Users user) {
        UsersResponseDTO dto = new UsersResponseDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAccess(user.getRole());
        dto.setLastLogin((user.getLastLogin() != null)?String.valueOf(user.getLastLogin()).replace(".0", ""):null);
        dto.setDateAdded(String.valueOf(user.getCreatedAt()).replace(".0", ""));
        return dto;
    }

    @Override
    public ResponseEntity<?> createUser(String fullName, String email, String access, String password, String[] storeIds, String userId) {
        logger.info("UserServiceImpl:: createUser :: Entered into the method.." + userId);
        CommonResponseDTO response = new CommonResponseDTO();
        try {
            String validation = usersValidation.saveUserValidation(fullName, email, access, storeIds);

            if (validation != null) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(validation);
                return ResponseEntity.status(Constants.BAD_REQUEST).body(response);
            }

            Users existByEmailId = usersRepository.findByEmail(email);
            if (existByEmailId != null && existByEmailId.getId() != null) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(Constants.USER_ALREADY_EXIST_FROM_GIVEN_EMAIL);
                return ResponseEntity.status(400).body(response);
            }

            Users users = new Users();
            users.setFullname(fullName);
            users.setUsername(fullName);
            users.setEmail(email);
            users.setPassword(bCryptPasswordEncoder.encode(password));
            users.setRole(access);
            users.setCreatedBy(userId);
            users.setUpdatedBy(userId);

            Users usersEntity = usersRepository.save(users);

            // Reuse the common store mapping method
            List<StoreMapping> storeMappings = mapStoresToUser(storeIds, usersEntity.getId(), userId);
            List<StoreMapping> storeMappingsEntity = storeMappingRepository.saveAll(storeMappings);

            if (storeMappingsEntity.size() > 0) {
                response.setStatusCode(Constants.CREATED);
                response.setStatus(Constants.CREATED_MESSAGE);
                response.setStatusMessage(Constants.USER_CREATED_SUCCESSFULLY);
                return ResponseEntity.status(201).body(response);
            } else {
                response.setStatusCode(Constants.INTERNAL_SERVER_ERROR);
                response.setStatus(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
                response.setStatusMessage(Constants.EXCEPTION_MADE);
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            logger.error("UserServiceImpl:: createUser :: Exception made.." + e);
            e.printStackTrace();
            response.setStatusCode(Constants.INTERNAL_SERVER_ERROR);
            response.setStatus(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
            response.setStatusMessage(Constants.EXCEPTION_MADE);
            return ResponseEntity.status(Constants.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @Transactional
    @Override
    public ResponseEntity<?> editUser(UserRequestDTO requestDTO, String updatingUserId) {
        CommonResponseDTO response = new CommonResponseDTO();
        try {
            String validation = usersValidation.saveUserValidation(requestDTO.getFullname(), requestDTO.getEmail(), requestDTO.getAccess(), requestDTO.getStores());
            String editUserId =  requestDTO.getId();

            if (validation != null) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(validation);
                return ResponseEntity.status(Constants.BAD_REQUEST).body(response);
            }

            Optional<Users> optionalUser = usersRepository.findById(editUserId);
            if (optionalUser.isPresent()) {
                Users users = optionalUser.get();
                users.setFullname(requestDTO.getFullname());
                users.setEmail(requestDTO.getEmail());
                users.setRole(requestDTO.getAccess());
                users.setUpdatedBy(updatingUserId);

                Users usersEntity = usersRepository.save(users);

                List<StoreMapping> storeMappings = storeMappingRepository.findByUserId(optionalUser.get().getId());
                if (!storeMappings.isEmpty()) {
                    storeMappingRepository.deleteAllByUserId(usersEntity.getId());
                }else{
                    response.setStatusCode(Constants.NOT_FOUND);
                    response.setStatus(Constants.NOT_FOUND_MESSAGE);
                    response.setStatusMessage(Constants.STORE_NOT_FOUND);
                    return ResponseEntity.status(Constants.NOT_FOUND).body(response);
                }

                List<StoreMapping> storeMappingsMap = mapStoresToUser(requestDTO.getStores(), usersEntity.getId(), updatingUserId);
                storeMappingRepository.saveAll(storeMappingsMap);

                response.setStatusCode(Constants.OK);
                response.setStatus(Constants.OK_MESSAGE);
                response.setStatusMessage(Constants.USER_UPDATED_SUCCESSFULLY);
                return ResponseEntity.status(Constants.OK).body(response);
            } else {
                response.setStatusCode(Constants.NOT_FOUND);
                response.setStatus(Constants.NOT_FOUND_MESSAGE);
                response.setStatusMessage(Constants.USER_NOT_FOUND);
                return ResponseEntity.status(Constants.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("UserServiceImpl:: editUser :: Exception made.." + e);
            e.printStackTrace();
            response.setStatusCode(Constants.INTERNAL_SERVER_ERROR);
            response.setStatus(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
            response.setStatusMessage(Constants.EXCEPTION_MADE);
            return ResponseEntity.status(Constants.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    private List<StoreMapping> mapStoresToUser(String[] storeIds, String userId, String createdBy) {
        List<StoreMapping> storeMappings = new ArrayList<>();

        for (String storeId : storeIds) {
            StoreMapping storeMapping = new StoreMapping();
            Stores stores = new Stores();
            stores.setId(storeId);
            storeMapping.setStores(stores);

            Users user = new Users();
            user.setId(userId);
            storeMapping.setUser(user);

            storeMapping.setCreatedBy(createdBy);
            storeMapping.setUpdatedBy(createdBy);
            storeMappings.add(storeMapping);
        }

        return storeMappings;
    }


    @Override
    @DeleteMapping
    public ResponseEntity<?> deleteUser(@PathVariable String id){

        logger.info("UserServiceImpl:: deleteUser :: Enter into the method...");
        CommonResponseDTO response = new CommonResponseDTO();
        try {
            Optional<Users> users = usersRepository.findById(id);
//            List<StoreMapping> store = storeMappingRepository.findByUserId(id);
//            if (!store.isEmpty()) {
//                storeMappingRepository.deleteById(id);
//            }

            if (users.isEmpty()) {
                response.setStatusCode(Constants.BAD_REQUEST);
                response.setStatus(Constants.BAD_REQUEST_MESSAGE);
                response.setStatusMessage(Constants.USER_NOT_FOUND);
                return ResponseEntity.badRequest().body(response);
            } else {
                users.get().setStatus(Constants.INACTIVE);
                usersRepository.save(users.get());
//                usersRepository.deleteById(id);
                logger.info("User with id: {} has been deleted", id);
                response.setStatusCode(Constants.OK);
                response.setStatus(Constants.OK_MESSAGE);
                response.setStatusMessage("User deleted successfully");
                return ResponseEntity.ok().body(response);
            }
        } catch (Exception exception) {
            logger.error("UserServiceImpl:: deleteUser :: Exception made.." + exception);
            exception.printStackTrace();
            response.setStatusCode(Constants.INTERNAL_SERVER_ERROR);
            response.setStatus(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
            response.setStatusMessage(Constants.EXCEPTION_MADE);
            return ResponseEntity.status(Constants.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}