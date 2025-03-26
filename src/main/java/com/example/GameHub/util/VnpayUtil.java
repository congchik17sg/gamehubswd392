package com.example.GameHub.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class VnpayUtil {

    public static String generatePaymentUrl(String payUrl, String tmnCode, String hashSecret, String version, String command,
                                            int amount, String orderInfo, String txnRef, String ipAddress, String returnUrl) throws Exception {
        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", version);
        params.put("vnp_Command", command);
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Amount", String.valueOf(amount * 100));
        params.put("vnp_OrderInfo", URLEncoder.encode(orderInfo, StandardCharsets.UTF_8.toString()));
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_IpAddr", ipAddress);
        params.put("vnp_ReturnUrl", URLEncoder.encode(returnUrl, StandardCharsets.UTF_8.toString()));

        // 🔹 Bổ sung các tham số quan trọng
        params.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_Locale", "vn");
        params.put("vnp_OrderType", "other");

        // 🔹 Tạo chuỗi query
        String query = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        // 🔹 Tạo SecureHash
        String secureHash = hmacSHA512(hashSecret, query);

        return payUrl + "?" + query + "&vnp_SecureHash=" + secureHash;
    }


    // 🔹 Xác thực chữ ký VNPAY
    public static boolean isValidSignature(Map<String, String[]> params, String secretKey) {
        Map<String, String> sortedParams = params.entrySet().stream()
                .filter(e -> !"vnp_SecureHash".equals(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0], (oldValue, newValue) -> oldValue, TreeMap::new));

        String query = sortedParams.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        String expectedHash = hmacSHA512(secretKey, query);
        return expectedHash.equalsIgnoreCase(params.get("vnp_SecureHash")[0]); // IgnoreCase để tránh lỗi so sánh hash
    }

    // 🔹 HMAC SHA512 Hash
    private static String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKey);
            byte[] hashBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC SHA512", e);
        }
    }
}
