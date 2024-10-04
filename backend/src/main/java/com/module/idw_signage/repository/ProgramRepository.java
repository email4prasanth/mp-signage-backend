package com.module.idw_signage.repository;

import com.module.idw_signage.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, String> {
    Optional<Program>  findByProgramName(String programName);

    List<Program> findByStoresId(String storeId);
}
