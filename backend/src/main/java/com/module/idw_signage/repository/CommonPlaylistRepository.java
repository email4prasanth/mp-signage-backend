package com.module.idw_signage.repository;

import com.module.idw_signage.model.CommonPlaylist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CommonPlaylistRepository extends JpaRepository<CommonPlaylist, String> {


   List <CommonPlaylist> findByProgramId(String programId);

    Page<CommonPlaylist> findByProgramNameContaining(String search, PageRequest pageRequest);
}
