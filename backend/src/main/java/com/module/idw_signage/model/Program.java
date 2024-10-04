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
public class Program {

    @Id
    private String id;

    @ManyToOne  // Specify the relationship type
    @JoinColumn(name = "store_id", referencedColumnName = "id", nullable = false)
    private Stores stores;

    @Column(name = "program_name")
    private String programName;

    @Column(name = "status")
    private String status;

    @Column(name = "link")
    private String link;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "expiry_date")
    private Date expiryDate;

    @Column(name = "format")
    private String format;

    @Column(name = "duration")
    private String duration;

    @Column(name = "file_size")
    private String fileSize;

    @Column(name = "role")
    private String role;

    @Column(name = "created_by")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)  // To define the timestamp format
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Column(name = "udpated_by")
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

        this.updatedAt = new Date();  // Update on entity modification
    }


}
