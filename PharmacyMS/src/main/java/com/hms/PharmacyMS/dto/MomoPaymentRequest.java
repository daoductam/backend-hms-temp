package com.hms.PharmacyMS.dto;
import lombok.*;

@Getter
@Setter
@Builder
public class MomoPaymentRequest {
    private String partnerCode;
    private String requestId;
    private long amount;
    private String orderId;
    private String orderInfo;
    private String redirectUrl;
    private String ipnUrl;
    private String requestType;
    private String extraData;
    private String lang;
    private String signature;
}