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
public class SaleResponseDTO {
    private Long saleId;        // ID của đơn hàng vừa tạo
    private String status;      // Trạng thái (PENDING, PAID)
    private BigDecimal totalAmount; // Tổng tiền
    private String paymentUrl;  // Quan trọng: Link để frontend redirect user sang Momo/VNPay
}