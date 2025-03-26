package com.example.GameHub.service;

import com.example.GameHub.entities.Product;
import com.example.GameHub.entities.Rating;
import com.example.GameHub.entities.User;
import com.example.GameHub.model.request.RatingRequest;
import com.example.GameHub.model.response.RatingResponse;
import com.example.GameHub.model.response.ResponseObject;
import com.example.GameHub.repository.ProductRepository;
import com.example.GameHub.repository.RatingRepository;
import com.example.GameHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ResponseEntity<ResponseObject> addRating(RatingRequest request, User user) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Rating rating = new Rating();
        rating.setUser(user);
        rating.setProduct(product);
        rating.setStars(request.getStars());
        rating.setComment(request.getComment());
        rating.setCreateAt(LocalDateTime.now());
        rating.setUpdateAt(LocalDateTime.now());

        ratingRepository.save(rating);

        RatingResponse response = new RatingResponse(
                rating.getRatingId(),
                user.getId(),
                product.getId(),
                rating.getStars(),
                rating.getComment(),
                rating.getCreateAt()
        );

        return ResponseEntity.ok(new ResponseObject("ok", "Rating added successfully", response));
    }

    public ResponseEntity<ResponseObject> getRatingsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<RatingResponse> ratings = ratingRepository.findByProduct(product).stream()
                .map(rating -> new RatingResponse(
                        rating.getRatingId(),
                        rating.getUser().getId(),
                        rating.getProduct().getId(),
                        rating.getStars(),
                        rating.getComment(),
                        rating.getCreateAt()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ResponseObject("ok", "Fetched ratings", ratings));
    }

    public ResponseEntity<ResponseObject> deleteRating(Long ratingId, User user) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));

        if (!rating.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseObject("failed", "Unauthorized to delete this rating", null));
        }

        ratingRepository.delete(rating);
        return ResponseEntity.ok(new ResponseObject("ok", "Rating deleted successfully", null));
    }
}
