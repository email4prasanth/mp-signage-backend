package com.module.idw_signage.repository;

import com.module.idw_signage.model.PowerSchedulerTerminalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PowerSchedulerTerminalDetailsRepository extends JpaRepository<PowerSchedulerTerminalDetails, String> {
}
