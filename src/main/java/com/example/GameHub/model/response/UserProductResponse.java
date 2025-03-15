package com.example.GameHub.model.response;


import com.example.GameHub.entities.UserProduct;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProductResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long productId;
    private String productTitle;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime purchaseDate;

    public UserProductResponse(UserProduct userProduct) {
        this.id = userProduct.getId();
        this.userId = userProduct.getUser().getId();
        this.username = userProduct.getUser().getUsername();
        this.productId = userProduct.getProduct().getId();
        this.productTitle = userProduct.getProduct().getProductTitle();
        this.purchaseDate = userProduct.getPurchaseDate();
    }

}
