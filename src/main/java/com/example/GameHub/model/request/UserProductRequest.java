package com.example.GameHub.model.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProductRequest {
    private Long userId;
    private Long productId;

}
