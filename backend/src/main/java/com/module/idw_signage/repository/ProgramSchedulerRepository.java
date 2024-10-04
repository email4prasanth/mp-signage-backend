package com.module.idw_signage.repository;

import com.module.idw_signage.dto.ProgramSchedulerDTO;
import com.module.idw_signage.model.ProgramScheduler;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProgramSchedulerRepository extends JpaRepository<ProgramScheduler, String> {
    boolean existsByProgramId(String programId);

    @Query(value = """
    SELECT 
    ps.id AS program_scheduler_id,
    ps.program_id AS program_id,
    ps.schedule_frequency, 
    ps.start_date, 
    ps.end_date, 
    ps.play_start_time, 
    ps.play_end_time,
    ps.play_mode,
    ps.timings, 
    ps.program_overlap, 
    ps.download_time, 
    ps.download_start_time,
    ps.download_end_time, 
    ps.created_at,
    ps.id as program_sche_id,
    -- Grouping store details with custom separator
    GROUP_CONCAT(DISTINCT CONCAT(store.id, ':$:', store.store_name, ':$:',sd.id ) SEPARATOR '$#$') AS store_mappings,
    -- Grouping terminal details with custom separator
    GROUP_CONCAT(DISTINCT CONCAT(td.program_scheduler_store_id, ':$:' ,termi.id, ':$:', termi.terminal_name) SEPARATOR '$#$') AS terminal_mappings,
    ps.week_days
FROM 
	signage_db.program as p 
left join 
    signage_db.program_scheduler AS ps on p.id = ps.program_id
LEFT JOIN 
    signage_db.program_scheduler_store_details AS sd ON ps.id = sd.program_scheduler_id
LEFT JOIN 
    signage_db.stores AS store ON sd.store_id = store.id
LEFT JOIN 
    signage_db.program_scheduler_terminal_details td ON sd.id = td.program_scheduler_store_id
LEFT JOIN 
    signage_db.terminal AS termi ON td.terminal_id = termi.id
WHERE 
    p.id = :programId
GROUP BY 
    ps.id
ORDER BY 
    ps.created_at DESC;""", nativeQuery = true)
    List<Object[]> findProgramScheduleDetailsById(@Param("programId") String programId);

    Optional<ProgramScheduler> findByProgramId(String programId);

//    boolean existsByProgramIdAndIsCommonPlaylist(String programId,boolean isCommonPlaylist);
}

