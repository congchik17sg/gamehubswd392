package com.example.GameHub.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ProductRequest {
        private String productTitle;
        private String description;
        private Double price;
        private Double discountPrice;
        private String downloadLink;
        private String versionName;
        private Long categoryId;
        private String status;

    }

