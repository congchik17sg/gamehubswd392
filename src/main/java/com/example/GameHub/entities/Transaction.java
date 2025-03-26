package com.example.GameHub.entities;

import com.example.GameHub.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "user_product_id", nullable = false)
    UserProduct userProduct;

    BigDecimal amount;  // Số tiền thanh toán

    @Enumerated(EnumType.STRING)
    TransactionStatus transactionStatus;  // Trạng thái thanh toán

    String paymentMethod;  // Phương thức thanh toán (VNPAY)

    String vnpTxnRef;  // Mã giao dịch VNPAY

    String vnpResponseCode;  // Mã phản hồi từ VNPAY

    String vnpSecureHash;  // Chữ ký bảo mật VNPAY

    LocalDateTime createAt;
    LocalDateTime expireAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        expireAt = createAt.plusMinutes(15); // Hết hạn sau 15 phút
    }
}
