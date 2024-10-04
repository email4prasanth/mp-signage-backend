package com.module.idw_signage.repository;

import com.module.idw_signage.model.CommonSchedulerTerminalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonSchedulerTerminalDetailsRepository extends JpaRepository<CommonSchedulerTerminalDetails, String> {
}
