package com.example.GameHub.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private String productTitle;
    private String description;
    private Double price;
    private Double discountPrice;
    private String downloadLink;
    private String versionName;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createAt;
    private List<String> imageUrls;
}

