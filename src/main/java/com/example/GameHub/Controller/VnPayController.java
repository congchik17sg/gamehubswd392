package com.example.GameHub.Controller;

import com.example.GameHub.entities.Transaction;
import com.example.GameHub.entities.UserProduct;
import com.example.GameHub.enums.TransactionStatus;
import com.example.GameHub.repository.TransactionRepository;
import com.example.GameHub.repository.UserProductRepository;
import com.example.GameHub.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
public class VnPayController {

    @Autowired
    private VnPayService vnPayService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserProductRepository userProductRepository;

    /**
     * API tạo thanh toán VNPAY
     */
    @GetMapping("/create")
    public ResponseEntity<?> createPayment(@RequestParam int amount,
                                           @RequestParam Long userProductId,
                                           HttpServletRequest request) {
        try {
            String ipAddress = request.getRemoteAddr();
            if ("0:0:0:0:0:0:0:1".equals(ipAddress)) {
                ipAddress = "127.0.0.1";
            }

            Optional<UserProduct> userProductOpt = userProductRepository.findById(userProductId);
            if (userProductOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "❌ Sản phẩm không tồn tại!"));
            }

            UserProduct userProduct = userProductOpt.get();
            List<Transaction> transactions = transactionRepository.findByUserProduct(userProduct);

            boolean hasSuccessTransaction = transactions.stream()
                    .anyMatch(t -> "SUCCESS".equals(t.getTransactionStatus()));

            if (hasSuccessTransaction) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "❌ Sản phẩm đã được thanh toán!"));
            }


            // Tạo mã giao dịch
            String txnRef = vnPayService.generateTxnRef();

            // Tạo giao dịch mới
            Transaction transaction = Transaction.builder()
                    .userProduct(userProduct)
                    .amount(BigDecimal.valueOf(amount))
                    .transactionStatus(TransactionStatus.PENDING)
                    .paymentMethod("VNPAY")
                    .vnpTxnRef(txnRef)
                    .createAt(LocalDateTime.now())
                    .build();

            transactionRepository.save(transaction);

            // Tạo URL thanh toán
            String paymentUrl = vnPayService.createPaymentUrl(amount, "Thanh toán GameHub", ipAddress, txnRef);

            // Log URL để kiểm tra
            System.out.println("🔹 Payment URL: " + paymentUrl);

            return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "❌ Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Xử lý phản hồi từ VNPAY
     */
    @GetMapping("/vnpay-return")
    public ModelAndView handleReturnUrl(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> paramMap.put(key, value[0]));
        String vnpTxnRef = request.getParameter("vnp_TxnRef");
        String vnpResponseCode = request.getParameter("vnp_ResponseCode");

        ModelAndView modelAndView = new ModelAndView("payment-result");

        // Xác thực chữ ký
        boolean isValid = vnPayService.validateSignature(paramMap);
        if (!isValid) {
            modelAndView.addObject("status", "failed");
            modelAndView.addObject("message", "Invalid signature.");
            return modelAndView;
        }

        // Tìm giao dịch
        Optional<Transaction> transactionOpt = transactionRepository.findByVnpTxnRef(vnpTxnRef);
        if (transactionOpt.isEmpty()) {
            modelAndView.addObject("status", "not-found");
            modelAndView.addObject("message", "Transaction not found.");
            return modelAndView;
        }

        // Cập nhật trạng thái giao dịch
        Transaction transaction = transactionOpt.get();
        TransactionStatus status = "00".equals(vnpResponseCode) ? TransactionStatus.SUCCESS : TransactionStatus.FAILED;
        transaction.setTransactionStatus(status);
        transaction.setVnpResponseCode(vnpResponseCode);
        transactionRepository.save(transaction);

        // Trả về trang Thymeleaf với trạng thái giao dịch
        modelAndView.addObject("status", status == TransactionStatus.SUCCESS ? "success" : "failed");
        modelAndView.addObject("message", status == TransactionStatus.SUCCESS ? "Payment Successful!" : "Payment Failed!");


        String fullUrl = request.getRequestURL() + "?" + request.getQueryString();
        System.out.println("🔍 VNPAY RETURN URL: " + fullUrl);
        return modelAndView;
    }



    /**
     * Lấy danh sách giao dịch
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionRepository.findAll());
    }
}
