package com.hms.PharmacyMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentLinkResponse {
    private String orderId;     // Mã đơn hàng hệ thống Payment sinh ra (VD: 101_1702345678)
    private String payUrl;      // Đường dẫn thanh toán (quan trọng nhất)
    private BigDecimal amount;  // Số tiền xác nhận
    private String provider;    // "momo", "vnpay"

    // Các trường mở rộng nếu cần dùng sau này
    private String qrCodeUrl;   // Link ảnh QR (nếu có)
    private String deeplink;    // Link mở app (nếu có)
}