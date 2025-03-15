package com.example.GameHub.repository;

import com.example.GameHub.entities.UserProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProductRepository extends JpaRepository<UserProduct, Long> {
    List<UserProduct> findByUserId(Long userId);
    List<UserProduct> findByProductId(Long productId);
    Optional<UserProduct> findByUserIdAndProductId(Long userId, Long productId);
}
