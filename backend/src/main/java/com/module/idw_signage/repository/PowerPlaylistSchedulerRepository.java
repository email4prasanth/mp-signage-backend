package com.module.idw_signage.repository;

import com.module.idw_signage.model.PowerPlaylistScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PowerPlaylistSchedulerRepository extends JpaRepository<PowerPlaylistScheduler, String> {

    @Query(value = """
            SELECT\s
                pps.id AS power_playlist_scheduler_id,
                pps.power_playlist_id AS power_playlist_id,
                pps.schedule_frequency,\s
                pps.start_date,\s
                pps.end_date,\s
                pps.play_start_time,\s
                pps.play_end_time,
                pps.play_mode,
                pps.timings,\s
                pps.program_overlap,\s
                pps.download_time,\s
                pps.download_start_time,
                pps.download_end_time,\s
                pps.created_at,
                pps.id as program_sche_id,
                -- Grouping store details with custom separator
                GROUP_CONCAT(DISTINCT CONCAT(store.id, ':$:', store.store_name, ':$:',sd.id ) SEPARATOR '$#$') AS store_mappings,
                -- Grouping terminal details with custom separator
                GROUP_CONCAT(DISTINCT CONCAT(td.power_scheduler_store_id, ':$:' ,termi.id, ':$:', termi.terminal_name) SEPARATOR '$#$') AS terminal_mappings,
                pps.week_days
            FROM\s
            	signage_db.power_playlist as pp
            left join\s
                signage_db.power_playlist_scheduler AS pps on pp.id = pps.power_playlist_id
            LEFT JOIN\s
                signage_db.power_scheduler_store_details AS sd ON pps.id = sd.power_scheduler_id
            LEFT JOIN\s
                signage_db.stores AS store ON sd.store_id = store.id
            LEFT JOIN\s
                signage_db.power_scheduler_terminal_details td ON sd.id = td.power_scheduler_store_id
            LEFT JOIN\s
                signage_db.terminal AS termi ON td.terminal_id = termi.id
            WHERE\s
                pp.id = :powerPlaylistId
            GROUP BY\s
                pps.id
            ORDER BY\s
                pps.created_at DESC;""", nativeQuery = true)
    List<Object[]> findPowerPlaylistScheduleDetailsById(@Param("powerPlaylistId") String powerPlaylistId);
}
