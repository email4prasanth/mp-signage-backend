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
@Table(name = "common_playlist")
public class CommonPlaylist {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @ManyToOne
    @JoinColumn(name = "program_id", referencedColumnName = "id", nullable = false)
    private Program program;

    @Column(name = "program_name")
    private String programName;

    @Column(name = "status")
    private String status;

    @Column(name = "link")
    private String link;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "expiry_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    @Column(name = "format")
    private String format;

    @Column(name = "duration")
    private String duration;

    @Column(name = "file_size")
    private String fileSize;

    @Column(name = "role")
    private String role;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();  // Set on create
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

}

