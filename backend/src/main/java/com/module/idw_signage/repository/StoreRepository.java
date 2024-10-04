package com.module.idw_signage.repository;

import com.module.idw_signage.model.Stores;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Stores, String> {

    List<Stores> findAll(Sort sort);
    List<Stores> findAllByUserId(String userId, Sort sort);

    Page<Stores> findByStoreNameContainingOrStoreLocationContaining(String name, String location, PageRequest pageRequest);
    Page<Stores> findByUserIdAndStoreNameContainingOrStoreLocationContaining(String userId, String name, String location, PageRequest pageRequest);
    Page<Stores> findByUserId(String userId, PageRequest pageRequest);

    int countByStoreNameContainingOrStoreLocationContaining(String search, String search1);

    int countByUserIdAndStoreNameContainingOrStoreLocationContaining(String userId, String search, String search1);

    int countByUserId(String userId);

    int countByIdIn(List<String> storeIds);
}
