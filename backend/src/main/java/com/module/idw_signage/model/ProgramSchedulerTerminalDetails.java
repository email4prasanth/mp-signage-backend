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
public class ProgramSchedulerTerminalDetails {

    @Id
    private String id;

    public ProgramSchedulerTerminalDetails(String id) {
        this.id = id;
    }


    @ManyToOne  // Specify the relationship type
    @JoinColumn(name = "program_scheduler_store_id", referencedColumnName = "id", nullable = false)
    private ProgramSchedulerStoreDetails programSchedulerStoreDetails;

    @ManyToOne
    @JoinColumn(name = "terminal_id", referencedColumnName = "id", nullable = false)
    private Terminal terminal;

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
}
