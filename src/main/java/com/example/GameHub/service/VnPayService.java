package com.example.GameHub.service;

import com.example.GameHub.util.VnpayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VnPayService {

    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.payUrl}")
    private String vnp_PayUrl;

    @Value("${vnpay.returnUrl}")
    private String vnp_ReturnUrl;

    @Value("${vnpay.version}")
    private String vnp_Version;

    @Value("${vnpay.command}")
    private String vnp_Command;

    // 🔹 Sinh mã giao dịch duy nhất
    public String generateTxnRef() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    // 🔹 Tạo URL thanh toán VNPAY
    public String createPaymentUrl(int amount, String orderInfo, String ipAddress, String txnRef) throws Exception {
        return VnpayUtil.generatePaymentUrl(
                vnp_PayUrl, vnp_TmnCode, vnp_HashSecret, vnp_Version, vnp_Command,
                amount, orderInfo, txnRef, ipAddress, vnp_ReturnUrl
        );
    }

    public boolean validateSignature(Map<String, String> params) {
        // Chuyển đổi Map<String, String> sang Map<String, String[]> để tương thích với VnpayUtil
        Map<String, String[]> convertedParams = params.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new String[]{e.getValue()}));

        return VnpayUtil.isValidSignature(convertedParams, vnp_HashSecret);
    }
}
