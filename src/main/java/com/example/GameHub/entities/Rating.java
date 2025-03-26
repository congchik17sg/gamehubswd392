package com.example.GameHub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
    @Table(name = "rating")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Rating {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long ratingId;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @ManyToOne
        @JoinColumn(name = "game_id", nullable = false)
        private Product product;

        private Integer stars; // Số sao đánh giá (1-5)

        @Column(columnDefinition = "TEXT")
        private String comment; // Nội dung đánh giá

        private LocalDateTime createAt;
        private LocalDateTime updateAt;
    }

