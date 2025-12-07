package com.hms.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentLinkRequest {

    @NotBlank
    private Long saleId;      // ví dụ: "45" (service sẽ thêm timestamp phía sau)

    @NotNull
    @Positive
    private BigDecimal amount;

//    @NotBlank
//    private String orderInfo;

    @NotBlank
    private String providerName; // "momo" | "vnpay" | "chuyenkhoan"

    private String returnUrl;
}
