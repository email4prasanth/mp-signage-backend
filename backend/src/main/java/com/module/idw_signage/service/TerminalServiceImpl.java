package com.module.idw_signage.service;

import com.module.idw_signage.Utils.Constants;
import com.module.idw_signage.dto.CommonResponseDTO;
import com.module.idw_signage.dto.CommonResponseWithObjectDTO;
import com.module.idw_signage.dto.TerminalRequestDTO;
import com.module.idw_signage.model.Stores;
import com.module.idw_signage.model.Terminal;
import com.module.idw_signage.repository.StoreRepository;
import com.module.idw_signage.repository.TerminalRepository;
import com.module.idw_signage.service.TerminalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
public class TerminalServiceImpl implements TerminalService {
    private static final Logger logger = LoggerFactory.getLogger(TerminalServiceImpl.class);
    CommonResponseDTO errorResponse = new CommonResponseDTO();


    @Autowired
    private TerminalRepository terminalRepository;

    @Autowired
    private StoreRepository storesRepository;



    @Override
    public ResponseEntity<?> saveTerminal(TerminalRequestDTO terminalRequestDTO) {
        Map<String, Object> response = new HashMap<>();
        CommonResponseDTO successResponse = new CommonResponseDTO();
        CommonResponseDTO errorResponse = new CommonResponseDTO();

        // Null checks for terminalId and storeId
        if (terminalRequestDTO.getTerminalId() == null || terminalRequestDTO.getTerminalId().isEmpty()) {
            errorResponse.setStatus(Constants.BAD_REQUEST_MESSAGE);
            errorResponse.setStatusCode(Constants.BAD_REQUEST);
            errorResponse.setStatusMessage(Constants.INVALID_TERMINAL_ID);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        if (terminalRequestDTO.getStoreId() == null) {
            errorResponse.setStatus(Constants.BAD_REQUEST_MESSAGE);
            errorResponse.setStatusCode(Constants.BAD_REQUEST);
            errorResponse.setStatusMessage(Constants.INVALID_STORE_ID);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Check if terminalId already exists
        Optional<Terminal> existingTerminal = terminalRepository.findByTerminalId(terminalRequestDTO.getTerminalId());

        if (existingTerminal.isPresent()) {
            errorResponse.setStatus(Constants.CONFLICT_MESSAGE);
            errorResponse.setStatusCode(Constants.CONFLICT);
            errorResponse.setStatusMessage(Constants.TERMINAL_ALREADY_EXISTS_ID + terminalRequestDTO.getTerminalId());
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        // Fetch the store by storeId
        Optional<Stores> optionalStore = storesRepository.findById(terminalRequestDTO.getStoreId());

        if (optionalStore.isPresent()) {
            Stores store = optionalStore.get();

            Terminal terminal = new Terminal();
            terminal.setStore(store);
            terminal.setTerminalName(terminalRequestDTO.getTerminalName());
            terminal.setTerminalId(terminalRequestDTO.getTerminalId());
            terminal.setTerminalOrientation(terminalRequestDTO.getOrientation());
            terminalRepository.save(terminal);

            successResponse.setStatusCode(Constants.OK);
            successResponse.setStatus(Constants.OK_MESSAGE);
            successResponse.setStatusMessage(Constants.TERMINAL_SAVED);
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
        } else {
            errorResponse.setStatus(Constants.NOT_FOUND_MESSAGE);
            errorResponse.setStatusCode(Constants.NOT_FOUND);
            errorResponse.setStatusMessage(Constants.STORE_NOT_FOUND_ID + terminalRequestDTO.getStoreId());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }


    @Override
    public ResponseEntity<?> getTerminals(String storeId, int page, int limit, String search) {
        logger.info("Entering getTerminalsByStoreId method...");
        CommonResponseWithObjectDTO responseDTO = new CommonResponseWithObjectDTO();

        try {
          Sort sort = Sort.by(Sort.Order.desc("id"));
            if (page <= 0) {
                page = 1;
            }
            if (limit <= 0) {
                limit = 10; // Set default limit
            }
            PageRequest pageRequest = PageRequest.of(page - 1, limit,sort);
            Page<Terminal> terminalPage;

            if (search != null && !search.trim().isEmpty()) {
                terminalPage = terminalRepository.findByStoreIdAndTerminalNameContainingOrStoreIdAndTerminalIdContaining(
                        storeId, search, storeId, search, pageRequest);
            } else {
                terminalPage = terminalRepository.findByStoreId(storeId, pageRequest);
            }

            List<TerminalRequestDTO> terminalDTOS = terminalPage.stream().map(terminal -> {
                TerminalRequestDTO terminalDTO = new TerminalRequestDTO();
                terminalDTO.setTerminalId(terminal.getTerminalId());
                terminalDTO.setStoreId(terminal.getStore().getId());
                terminalDTO.setTerminalName(terminal.getTerminalName());
                terminalDTO.setOrientation(terminal.getTerminalOrientation());
                return terminalDTO;
            }).collect(Collectors.toList());



            if (terminalDTOS.isEmpty()) {
                errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
                errorResponse.setStatus("Bad Request");
                errorResponse.setStatusMessage("No terminals found");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setStatus("OK");
            responseDTO.setStatusMessage("Terminals fetched successfully");
            responseDTO.setData(terminalDTOS);
            responseDTO.setTotalRecords(terminalPage.getTotalElements());
            responseDTO.setTotalPages(terminalPage.getTotalPages());
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            errorResponse.setStatus("Bad Request");
            errorResponse.setStatusMessage("An error occurred while fetching terminals");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}



