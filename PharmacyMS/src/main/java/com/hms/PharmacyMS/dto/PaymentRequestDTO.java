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
public class PaymentRequestDTO {
    private Long saleId;        // ID đơn hàng cần thanh toán
    private BigDecimal amount;  // Số tiền cần thanh toán
    private String providerName;// Tên cổng thanh toán: "momo", "vnpay"
    private String returnUrl;   // URL để redirect user về frontend sau khi thanh toán
}