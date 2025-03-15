package com.example.GameHub.service;

import com.example.GameHub.entities.Product;
import com.example.GameHub.entities.User;
import com.example.GameHub.entities.UserProduct;
import com.example.GameHub.model.request.UserProductRequest;
import com.example.GameHub.model.response.ResponseObject;
import com.example.GameHub.model.response.UserProductResponse;
import com.example.GameHub.repository.ProductRepository;
import com.example.GameHub.repository.UserProductRepository;
import com.example.GameHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProductService {
    private final UserProductRepository userProductRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ResponseEntity<ResponseObject> addOwnership(UserProductRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (userProductRepository.findByUserIdAndProductId(user.getId(), product.getId()).isPresent()) {
            return ResponseEntity.badRequest().body(new ResponseObject("failed", "User already owns this product", null));
        }

        UserProduct userProduct = new UserProduct();
        userProduct.setUser(user);
        userProduct.setProduct(product);
        userProduct.setPurchaseDate(LocalDateTime.now());

        userProductRepository.save(userProduct);

        return ResponseEntity.ok(new ResponseObject("ok", "Ownership added successfully", new UserProductResponse(userProduct)));
    }

    public ResponseEntity<ResponseObject> getUserProducts(Long userId) {
        List<UserProduct> userProducts = userProductRepository.findByUserId(userId);
        List<UserProductResponse> responseList = userProducts.stream()
                .map(UserProductResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ResponseObject("ok", "Fetched user products", responseList));
    }

    public ResponseEntity<ResponseObject> getAllOwnerships() {
        List<UserProduct> ownerships = userProductRepository.findAll();

        List<UserProductResponse> responses = ownerships.stream().map(op ->
                new UserProductResponse(
                        op.getId(),
                        op.getUser().getId(),
                        op.getUser().getUsername(),
                        op.getProduct().getId(),
                        op.getProduct().getProductTitle(),
                        op.getPurchaseDate()
                )).collect(Collectors.toList());

        return ResponseEntity.ok(new ResponseObject("ok", "Lấy tất cả ownerships thành công", responses));
    }

    public ResponseEntity<ResponseObject> deleteOwnership(Long userId, Long productId) {
        UserProduct userProduct = userProductRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Ownership not found"));

        userProductRepository.delete(userProduct);

        return ResponseEntity.ok(new ResponseObject("ok", "Ownership deleted successfully", null));
    }

}

