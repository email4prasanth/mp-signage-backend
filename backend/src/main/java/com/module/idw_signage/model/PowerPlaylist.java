package com.module.idw_signage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "power_playlist")
public class PowerPlaylist {

    @Id
    @Column(name = "id", length = 55, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "store_id", referencedColumnName = "id", nullable = false)
    private Stores store;

    @Column(name = "program_name")
    private String programName;

    @Column(name = "duration")
    private String duration;

    @Column(name = "size")
    private String size;

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "prior")
    private String prior;

    @Column(name = "review")
    private String review;

    @Column(name = "play_frequency")
    private String playFrequency;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();  // Set on create
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();  // Update on entity modification
    }


}