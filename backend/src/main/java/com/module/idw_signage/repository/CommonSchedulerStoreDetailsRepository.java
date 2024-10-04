package com.module.idw_signage.repository;

import com.module.idw_signage.model.CommonSchedulerStoreDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonSchedulerStoreDetailsRepository extends JpaRepository<CommonSchedulerStoreDetails, String> {
}
