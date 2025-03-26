package com.example.GameHub.Controller;

import com.example.GameHub.entities.User;
import com.example.GameHub.model.request.RatingRequest;
import com.example.GameHub.model.response.ResponseObject;
import com.example.GameHub.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rating")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping("/add")
    public ResponseEntity<ResponseObject> addRating(@RequestBody RatingRequest request,
                                                    @AuthenticationPrincipal User user) {
        return ratingService.addRating(request, user);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ResponseObject> getRatingsByProduct(@PathVariable Long productId) {
        return ratingService.getRatingsByProduct(productId);
    }

    @DeleteMapping("/delete/{ratingId}")
    public ResponseEntity<ResponseObject> deleteRating(@PathVariable Long ratingId,
                                                       @AuthenticationPrincipal User user) {
        return ratingService.deleteRating(ratingId, user);
    }
}
