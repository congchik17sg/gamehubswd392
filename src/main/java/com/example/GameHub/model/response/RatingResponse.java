package com.example.GameHub.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RatingResponse {
    private Long id;
    private Long userId;
    private Long productId;
    private int stars;
    private String comment;
    private LocalDateTime createAt;
}
