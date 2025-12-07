package com.hms.payment.controller;

import com.hms.payment.dto.PaymentLinkRequest;
import com.hms.payment.dto.PaymentLinkResponse;
import com.hms.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // POST /create-link  (BookingService gọi)
    @PostMapping("/create-link")
    public ResponseEntity<PaymentLinkResponse> createPaymentLink(
            @Valid @RequestBody PaymentLinkRequest request,
            HttpServletRequest httpRequest
    ) {
        String clientIp = request.getIpAddr();
        if (clientIp == null || clientIp.isBlank()) {
            clientIp = httpRequest.getRemoteAddr();
        }
        PaymentLinkResponse response = paymentService.createPaymentLink(request, clientIp);
        return ResponseEntity.ok(response);
    }

    // POST /momo-callback  (Momo IPN)
    @PostMapping("/momo-callback")
    public ResponseEntity<String> momoCallback(@RequestParam Map<String, String> params) {
        paymentService.handleMomoCallback(params);
        return ResponseEntity.ok("OK");
    }

    // GET /vnpay-callback  (VNPay redirect)
    @GetMapping("/vnpay-callback")
    public ResponseEntity<String> vnpayCallback(@RequestParam Map<String, String> params) {
        paymentService.handleVnpayCallback(params);
        return ResponseEntity.ok("OK");
    }

    // GET /status/{orderId}  (Client hỏi trạng thái)
    @GetMapping("/status/{orderId}")
    public ResponseEntity<Map<String, String>> getPaymentStatus(@PathVariable String orderId) {
        String status = paymentService.getPaymentStatus(orderId);
        Map<String, String> body = new HashMap<>();
        body.put("status", status);
        return ResponseEntity.ok(body);
    }
}
