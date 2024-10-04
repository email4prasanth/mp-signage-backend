package com.module.idw_signage.repository;

import com.module.idw_signage.model.Terminal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TerminalRepository extends JpaRepository<Terminal, String> {

    Optional<Terminal> findByTerminalId(String terminalId);

    Page<Terminal> findByStoreId(String storeId, PageRequest pageRequest);

    Page<Terminal> findByStoreIdAndTerminalNameContainingOrStoreIdAndTerminalIdContaining(String storeId, String search, String storeId1, String search1, PageRequest pageRequest);

    int countByIdIn(List<String> terminalIds);

}
