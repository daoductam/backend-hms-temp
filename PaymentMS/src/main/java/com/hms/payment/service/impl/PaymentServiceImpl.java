package com.hms.payment.service.impl;

import com.hms.payment.dto.PaymentLinkRequest;
import com.hms.payment.dto.PaymentLinkResponse;
import com.hms.payment.entity.Payment;
import com.hms.payment.repository.PaymentRepository;
import com.hms.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    // tương đương BOOKING_SERVICE_URL = 'http://localhost:4003'
    @Value("${booking.service.url:http://localhost:4003}")
    private String bookingServiceUrl;

    @Override
    public PaymentLinkResponse createPaymentLink(PaymentLinkRequest req, String clientIp) {

        // Node: orderIdWithTimestamp = `${orderId}_${Date.now()}`
        String orderIdWithTs = req.getOrderId() + "_" + Instant.now().toEpochMilli();

        BigDecimal amount = req.getAmount();
        String providerName = req.getProviderName().toLowerCase();

        String payUrl = null;
        String qrCodeUrl = null;
        String deeplink = null;

        switch (providerName) {
            case "momo" -> {
                // TODO: gọi Momo real API & sinh chữ ký
                payUrl = "https://test-payment.momo.vn/payment/" + orderIdWithTs;
            }
            case "vnpay" -> {
                // TODO: build vnp_Params, ký SHA512, tạo VNPay URL thật
                payUrl = "https://sandbox.vnpayment.vn/payment/" + orderIdWithTs;
            }
            case "chuyenkhoan" -> {
                // TODO: gọi VietQR API thật để lấy QR code
                qrCodeUrl = "https://api.vietqr.io/img/" + orderIdWithTs;
            }
            default -> throw new IllegalArgumentException("Unsupported provider: " + providerName);
        }

        // Lưu Payment (tương đương Payment.create(...) trong Node)
        Payment payment = Payment.builder()
                .bookingId(parseBookingId(req.getOrderId()))
                .amount(amount)
                .provider(providerName)
                .transId(null)     // sẽ cập nhật khi callback
                .resultCode(null)  // cập nhật sau
                .build();
        paymentRepository.save(payment);

        return PaymentLinkResponse.builder()
                .provider(providerName)
                .orderId(orderIdWithTs)
                .amount(amount)
                .payUrl(payUrl)
                .qrCodeUrl(qrCodeUrl)
                .deeplink(deeplink)
                .build();
    }

    @Override
    public void handleMomoCallback(Map<String, String> data) {
        // TODO: verify chữ ký Momo giống code Node

        String orderIdWithTs = data.get("orderId");
        Integer resultCode = Integer.valueOf(data.get("resultCode"));
        String transId = data.get("transId");

        Long realBookingId = parseBookingId(orderIdWithTs);

        Payment payment = Payment.builder()
                .bookingId(realBookingId)
                .amount(new BigDecimal(data.getOrDefault("amount", "0")))
                .provider("momo")
                .transId(transId)
                .resultCode(resultCode)
                .build();
        paymentRepository.save(payment);

        if (resultCode == 0) {
            restTemplate.postForObject(
                    bookingServiceUrl + "/update-status",
                    Map.of("bookingId", realBookingId, "status", "CONFIRMED"),
                    Void.class
            );
        }
    }

    @Override
    public void handleVnpayCallback(Map<String, String> vnpParams) {
        // TODO: verify chữ ký VNPay giống code Node

        String txnRef = vnpParams.get("vnp_TxnRef"); // orderIdWithTs
        String responseCode = vnpParams.get("vnp_ResponseCode");
        String transId = vnpParams.get("vnp_TransactionNo");

        Long realBookingId = parseBookingId(txnRef);

        BigDecimal amount = BigDecimal.ZERO;
        try {
            amount = new BigDecimal(vnpParams.getOrDefault("vnp_Amount", "0"))
                    .divide(BigDecimal.valueOf(100)); // VNPay thường *100
        } catch (NumberFormatException ignored) {
        }

        Payment payment = Payment.builder()
                .bookingId(realBookingId)
                .amount(amount)
                .provider("vnpay")
                .transId(transId)
                .resultCode(responseCode != null ? Integer.valueOf(responseCode) : null)
                .build();
        paymentRepository.save(payment);

        if ("00".equals(responseCode)) {
            restTemplate.postForObject(
                    bookingServiceUrl + "/update-status",
                    Map.of("bookingId", realBookingId, "status", "CONFIRMED"),
                    Void.class
            );
        }
    }

    @Override
    public String getPaymentStatus(String orderIdWithTs) {
        Long realBookingId = parseBookingId(orderIdWithTs);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> resp = restTemplate.getForObject(
                    bookingServiceUrl + "/status/" + realBookingId,
                    Map.class
            );
            if (resp == null) return "NOT_FOUND";
            Object status = resp.get("status");
            return status != null ? status.toString() : "NOT_FOUND";
        } catch (Exception ex) {
            return "NOT_FOUND";
        }
    }

    private Long parseBookingId(String orderIdWithTs) {
        if (orderIdWithTs == null) return null;
        String idStr = orderIdWithTs.contains("_")
                ? orderIdWithTs.split("_")[0]
                : orderIdWithTs;
        return Long.valueOf(idStr);
    }
}
