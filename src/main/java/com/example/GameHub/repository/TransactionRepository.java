package com.example.GameHub.repository;

import com.example.GameHub.entities.Transaction;
import com.example.GameHub.entities.UserProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 🔹 Tìm giao dịch theo UserProduct (để kiểm tra sản phẩm đã thanh toán chưa)
    List<Transaction> findByUserProduct(UserProduct userProduct);


    // 🔹 Tìm giao dịch theo mã giao dịch VNPAY
    Optional<Transaction> findByVnpTxnRef(String vnpTxnRef);
}
