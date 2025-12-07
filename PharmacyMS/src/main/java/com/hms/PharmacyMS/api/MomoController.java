package com.hms.PharmacyMS.api;

import com.hms.PharmacyMS.dto.MomoPaymentResponse;
import com.hms.PharmacyMS.service.MomoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pharmacy/payment")
@CrossOrigin
@RequiredArgsConstructor
public class MomoController {

    private final MomoService momoService;

    @PostMapping("/create")
    public ResponseEntity<MomoPaymentResponse> createPayment(@RequestParam String orderId,
                                                             @RequestParam long amount) {
        try {
            // Gọi service tạo thanh toán
            MomoPaymentResponse response = momoService.createPayment(orderId, amount, "Thanh toan don hang " + orderId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API này MoMo sẽ gọi lại server của bạn (Webhook/IPN)
    @PostMapping("/ipn")
    public ResponseEntity<Void> ipnCallback(@RequestBody Map<String, Object> callbackData) {
        System.out.println("MoMo Callback: " + callbackData);
        // 1. Kiểm tra signature để đảm bảo request từ MoMo
        // 2. Kiểm tra resultCode (0 là thành công)
        // 3. Cập nhật trạng thái đơn hàng trong Database (Sale entity)

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}