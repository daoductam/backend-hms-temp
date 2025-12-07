package com.hms.PharmacyMS.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MomoPaymentResponse {
    private String partnerCode;
    private String orderId;
    private String requestId;
    private int amount;
    private long responseTime;
    private String message;
    private int resultCode;
    private String payUrl; // Link để redirect user sang MoMo
}