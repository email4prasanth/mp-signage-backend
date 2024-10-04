package com.module.idw_signage.repository;

import com.module.idw_signage.model.CommonPlaylistScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonPlaylistSchedulerRepository extends JpaRepository<CommonPlaylistScheduler, String> {

    @Query(value = """
            SELECT\s
                cps.id AS common_playlist_scheduler_id,
                cps.common_playlist_id AS common_playlist_id,
                cps.schedule_frequency,\s
                cps.start_date,\s
                cps.end_date,\s
                cps.play_start_time,\s
                cps.play_end_time,
                cps.play_mode,
                cps.timings,\s
                cps.program_overlap,\s
                cps.download_time,\s
                cps.download_start_time,
                cps.download_end_time,\s
                cps.created_at,
                cps.id as program_sche_id,
                -- Grouping store details with custom separator
                GROUP_CONCAT(DISTINCT CONCAT(store.id, ':$:', store.store_name, ':$:',sd.id ) SEPARATOR '$#$') AS store_mappings,
                -- Grouping terminal details with custom separator
                GROUP_CONCAT(DISTINCT CONCAT(td.common_scheduler_store_id, ':$:' ,termi.id, ':$:', termi.terminal_name) SEPARATOR '$#$') AS terminal_mappings,
                cps.week_days
            FROM\s
            	signage_db.common_playlist as cp
            left join\s
                signage_db.common_playlist_scheduler AS cps on cp.id = cps.common_playlist_id
            LEFT JOIN\s
                signage_db.common_scheduler_store_details AS sd ON cps.id = sd.common_scheduler_id
            LEFT JOIN\s
                signage_db.stores AS store ON sd.store_id = store.id
            LEFT JOIN\s
                signage_db.common_scheduler_terminal_details td ON sd.id = td.common_scheduler_store_id
            LEFT JOIN\s
                signage_db.terminal AS termi ON td.terminal_id = termi.id
            WHERE\s
                cp.id = :commonPlaylistId
            GROUP BY\s
                cps.id
            ORDER BY\s
                cps.created_at DESC;""", nativeQuery = true)
    List<Object[]> findCommonPlaylistScheduleDetailsById(@Param("commonPlaylistId") String commonPlaylistId);
}
