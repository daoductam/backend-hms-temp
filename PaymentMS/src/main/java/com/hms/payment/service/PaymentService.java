package com.hms.payment.service;

import com.hms.payment.dto.PaymentLinkRequest;
import com.hms.payment.dto.PaymentLinkResponse;

import java.util.Map;

public interface PaymentService {

    PaymentLinkResponse createPaymentLink(PaymentLinkRequest request, String clientIp);

    void handleMomoCallback(Map<String, String> params);

//    void handleVnpayCallback(Map<String, String> params);

//    String getPaymentStatus(String orderId);
}
