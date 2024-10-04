package com.module.idw_signage.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Media {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "store_id",  referencedColumnName = "id",nullable = false)
    private Stores stores;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "size")
    private int size;

    @Column(name = "duration")
    private String duration;

    @Column(name = "uploader")
    private String uploader;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiry_date")
    private Date expiryDate;

    @Column(name = "link")
    private String link;

    @Column(name = "checker")
    private String checker;

    @Column(name = "status")
    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        this.createdDate = new Date();
        this.updatedAt = new Date();  // Set on create
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    @Column(name = "created_by")
    private String createdBy;

}





