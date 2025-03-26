package com.example.GameHub.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingRequest {
    private Long productId;
    private int stars;
    private String comment;
}

