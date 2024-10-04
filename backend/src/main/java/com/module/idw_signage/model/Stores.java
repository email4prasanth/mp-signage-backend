package com.module.idw_signage.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Stores {

    @Id
    private String id;

    @ManyToOne  // Specify the relationship type
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private Users user;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "store_category")
    private String storeCategory;

    @Column(name = "store_location")
    private String storeLocation;

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
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();  // Update on entity modification
    }

    public Stores(String id) {
        this.id = id;
    }
}
