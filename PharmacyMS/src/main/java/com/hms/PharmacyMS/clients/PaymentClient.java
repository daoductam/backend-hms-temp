package com.hms.PharmacyMS.clients;

import com.hms.PharmacyMS.dto.PaymentLinkResponse; // Cần tạo DTO này bên Pharmacy để hứng response
import com.hms.PharmacyMS.dto.PaymentRequestDTO;  // Cần tạo DTO request
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE") // Tên service Payment trong Eureka
public interface PaymentClient {

    @PostMapping("/create-link")
    PaymentLinkResponse createPayment(@RequestBody PaymentRequestDTO request);
}