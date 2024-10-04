package com.module.idw_signage.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BaseEntity {

    private Timestamp createdAt;
    private Timestamp updatedAt;
}
