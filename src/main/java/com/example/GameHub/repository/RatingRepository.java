package com.example.GameHub.repository;

import com.example.GameHub.entities.Product;
import com.example.GameHub.entities.Rating;
import com.example.GameHub.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByProduct(Product product);
  //  List<Rating> findByUser(User user);
   // Optional<Rating> findByUserAndGame(User user, Product product); // Check user đã rating game này chưa
}