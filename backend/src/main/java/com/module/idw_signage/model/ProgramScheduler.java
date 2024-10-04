package com.module.idw_signage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProgramScheduler {

    @Id
    private String id;

    @ManyToOne  // Specify the relationship type
    @JoinColumn(name = "program_id", referencedColumnName = "id", nullable = false)
    private Program program;

    @Column(name = "schedule_frequency")
    private String scheduleFrequency;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "play_start_time")
    private String playStartTime;

    @Column(name = "play_end_time")
    private String playEndTime;

    @Column(name = "play_mode")
    private String playMode;

    @Column(name = "timings")
    private String timings;

    @Column(name = "program_overlap")
    private String programOverlap;

    @Column(name = "download_time")
    private String downloadTime;

    @Column(name = "download_start_time")
    private String downloadStartTime;

    @Column(name = "download_end_time")
    private String downloadEndTime;

    @Column(name = "week_days")
    private String weekDays;

    @Column(name = "created_by")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)  // To define the timestamp format
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();  // Set on create
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    public ProgramScheduler(String id) {
        this.id = id;
    }

}
