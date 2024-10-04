package com.module.idw_signage.repository;

import com.module.idw_signage.model.PowerPlaylist;
import com.module.idw_signage.model.Stores;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PowerPlaylistRepository extends JpaRepository<PowerPlaylist, String> {

    Optional<PowerPlaylist> findByProgramNameAndStore(String programName, Stores store);

    Page<PowerPlaylist> findByStoreIdAndProgramNameContainingOrStoreIdAndReviewContaining(String storeId, String search, String storeId1, String search1, PageRequest pageRequest);

    Page<PowerPlaylist> findByStoreId(String storeId, PageRequest pageRequest);
}
