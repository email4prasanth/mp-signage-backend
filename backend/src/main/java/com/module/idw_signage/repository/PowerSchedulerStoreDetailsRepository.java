package com.module.idw_signage.repository;

import com.module.idw_signage.model.PowerSchedulerStoreDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PowerSchedulerStoreDetailsRepository extends JpaRepository<PowerSchedulerStoreDetails, String> {
}
