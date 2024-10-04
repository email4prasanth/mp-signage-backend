package com.module.idw_signage.repository;

import com.module.idw_signage.model.StoreMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreMappingRepository extends JpaRepository<StoreMapping, String> {
    void deleteByUserId(String id);

    List<StoreMapping> findByUserId(String id);

    void deleteAllByUserId(String id);
}
