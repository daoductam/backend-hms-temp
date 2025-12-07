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

    // URL của Pharmacy Service (cấu hình trong application.properties)
    // Ví dụ: http://localhost:8082/pharmacy/sales/internal/confirm
    @Value("${service.pharmacy.confirm-url}")
    private String pharmacyConfirmUrl;

    @Override
    public PaymentLinkResponse createPaymentLink(PaymentLinkRequest req, String clientIp) {
        // Tạo mã đơn hàng unique cho cổng thanh toán: SALEID_TIMESTAMP
        String orderIdWithTs = req.getSaleId() + "_" + System.currentTimeMillis();

        // --- GIẢ LẬP LOGIC TẠO URL (Bạn thay code thật của Momo/VNPay vào đây) ---
        String payUrl = "https://test-payment.momo.vn/v2/gateway/api/create?orderId=" + orderIdWithTs;
        // ------------------------------------------------------------------------

        // Lưu thông tin thanh toán ban đầu
        Payment payment = Payment.builder()
                .saleId(req.getSaleId())
                .amount(req.getAmount())
                .provider(req.getProviderName())
                .build();
        paymentRepository.save(payment);

        return PaymentLinkResponse.builder()
                .orderId(orderIdWithTs)
                .payUrl(payUrl)
                .amount(req.getAmount())
                .build();
    }

    @Override
    public void handleMomoCallback(Map<String, String> data) {
        // 1. Lấy thông tin từ Webhook Momo
        String orderIdWithTs = data.get("orderId"); // VD: 101_1702345678
        String transId = data.get("transId");
        int resultCode = Integer.parseInt(data.get("resultCode"));

        // 2. Tách lấy Sale ID gốc
        Long realSaleId = Long.parseLong(orderIdWithTs.split("_")[0]);

        // 3. Tìm Payment trong DB (bản ghi mới nhất của saleId này)
        // Lưu ý: Nên dùng findTopBySaleIdOrderByIdDesc trong Repository
        Payment payment = paymentRepository.findTopBySaleIdOrderByIdDesc(realSaleId)
                .orElseThrow(() -> new RuntimeException("Payment info not found for Sale ID: " + realSaleId));

        // 4. Cập nhật kết quả thanh toán
        payment.setTransId(transId);
        payment.setResultCode(resultCode);
        paymentRepository.save(payment);

        // 5. Nếu thành công (resultCode = 0) -> Gọi PharmacyMS để update Sale
        if (resultCode == 0) {
            try {
                // Gọi API nội bộ của Pharmacy
                restTemplate.postForObject(
                        pharmacyConfirmUrl + "?saleId=" + realSaleId,
                        null,
                        Void.class
                );
                System.out.println("Updated Pharmacy Order Status: SUCCESS");
            } catch (Exception e) {
                System.err.println("Failed to call PharmacyMS: " + e.getMessage());
                // Có thể implement cơ chế Retry ở đây (Queue/Job)
            }
        }
    }


}
