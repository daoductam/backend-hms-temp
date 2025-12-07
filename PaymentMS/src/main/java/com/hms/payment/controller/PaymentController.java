package com.hms.payment.controller;

import com.hms.payment.dto.BaseResponse; // Import BaseResponse
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
@RequestMapping("/") // Có thể đổi thành /api/v1/payment để chuẩn RESTful hơn nếu muốn
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // POST /create-link
    // API này giao tiếp nội bộ hoặc với Client -> Cần bọc BaseResponse
    @PostMapping("/create-link")
    public ResponseEntity<BaseResponse<PaymentLinkResponse>> createPaymentLink(
            @Valid @RequestBody PaymentLinkRequest request,
            HttpServletRequest httpRequest
    ) {
        String clientIp = request.getIpAddr();
        if (clientIp == null || clientIp.isBlank()) {
            clientIp = httpRequest.getRemoteAddr();
        }
        PaymentLinkResponse responseData = paymentService.createPaymentLink(request, clientIp);

        // Wrap vào BaseResponse
        return ResponseEntity.ok(BaseResponse.success(responseData, "Tạo link thanh toán thành công"));
    }

    // POST /momo-callback (Momo IPN)
    // GIỮ NGUYÊN: Bên thứ 3 gọi, không sửa format trả về
    @PostMapping("/momo-callback")
    public ResponseEntity<String> momoCallback(@RequestParam Map<String, String> params) {
        paymentService.handleMomoCallback(params);
        return ResponseEntity.ok("OK"); // Trả về format mà Momo yêu cầu
    }

    // GET /vnpay-callback (VNPay redirect)
    // GIỮ NGUYÊN hoặc có thể điều hướng về Frontend
    @GetMapping("/vnpay-callback")
    public ResponseEntity<String> vnpayCallback(@RequestParam Map<String, String> params) {
        paymentService.handleVnpayCallback(params);
        return ResponseEntity.ok("OK");
    }

    // GET /status/{orderId}
    // API Client hỏi trạng thái -> Cần bọc BaseResponse
    @GetMapping("/status/{orderId}")
    public ResponseEntity<BaseResponse<Map<String, String>>> getPaymentStatus(@PathVariable String orderId) {
        String status = paymentService.getPaymentStatus(orderId);
        Map<String, String> body = new HashMap<>();
        body.put("status", status);

        return ResponseEntity.ok(BaseResponse.success(body, "Lấy trạng thái giao dịch thành công"));
    }
}