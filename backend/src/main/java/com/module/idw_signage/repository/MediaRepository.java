package com.module.idw_signage.repository;

import com.module.idw_signage.dto.MediaDTO;
import com.module.idw_signage.model.Media;
import com.module.idw_signage.model.Stores;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, String> {

  List<Media> findByStoresId(String storeId);

    Optional<Media> findByFileNameAndStores(String fileName, Stores store);

    Page<Media> findByStoresId(String storeId, Pageable pageable);
    Page<Media> findByStoresIdAndFileNameContainingOrStoresIdAndLinkContaining(String storeId, String search, String storeId1, String search1, PageRequest pageRequest);
}
