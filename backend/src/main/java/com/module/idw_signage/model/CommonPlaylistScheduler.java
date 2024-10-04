package com.module.idw_signage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "common_playlist_scheduler")
public class CommonPlaylistScheduler {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "common_playlist_id", referencedColumnName = "id", nullable = false)
    private CommonPlaylist commonPlaylist;

    @Column(name = "schedule_frequency")
    private String scheduleFrequency;

    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Column(name = "play_start_time")
    @Temporal(TemporalType.TIME)
    private Date playStartTime;

    @Column(name = "play_end_time")
    @Temporal(TemporalType.TIME)
    private Date playEndTime;

    @Column(name = "play_mode")
    private String playMode;

    @Column(name = "timings")
    private String timings;

    @Column(name = "program_overlap")
    private String programOverlap;

    @Column(name = "download_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date downloadTime;

    @Column(name = "download_start_time")
    @Temporal(TemporalType.TIME)
    private Date downloadStartTime;

    @Column(name = "download_end_time")
    @Temporal(TemporalType.TIME)
    private Date downloadEndTime;

    @Column(name = "week_days")
    private String weekDays;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}
