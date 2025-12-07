package com.hms.PharmacyMS.service;

import com.google.gson.Gson;
import com.hms.PharmacyMS.dto.MomoPaymentRequest;
import com.hms.PharmacyMS.dto.MomoPaymentResponse;
import com.hms.PharmacyMS.util.MomoEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MomoService {

    @Value("${momo.endpoint}")
    private String endpoint;
    @Value("${momo.partnerCode}")
    private String partnerCode;
    @Value("${momo.accessKey}")
    private String accessKey;
    @Value("${momo.secretKey}")
    private String secretKey;
    @Value("${momo.returnUrl}")
    private String returnUrl;
    @Value("${momo.ipnUrl}")
    private String ipnUrl;

    public MomoPaymentResponse createPayment(String orderId, long amount, String orderInfo) throws Exception {
        String requestId = String.valueOf(System.currentTimeMillis());
        String requestType = "captureWallet";
        String extraData = "";

        // Format bắt buộc của MoMo: key=value&key=value... (theo thứ tự a-z)
        String rawSignature = "accessKey=" + accessKey
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + ipnUrl
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + partnerCode
                + "&redirectUrl=" + returnUrl
                + "&requestId=" + requestId
                + "&requestType=" + requestType;

        String signature = MomoEncoder.signHmacSHA256(rawSignature, secretKey);

        MomoPaymentRequest requestDTO = MomoPaymentRequest.builder()
                .partnerCode(partnerCode)
                .requestId(requestId)
                .amount(amount)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .redirectUrl(returnUrl)
                .ipnUrl(ipnUrl)
                .requestType(requestType)
                .extraData(extraData)
                .lang("vi")
                .signature(signature)
                .build();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(new Gson().toJson(requestDTO), headers);

        return restTemplate.postForObject(endpoint, entity, MomoPaymentResponse.class);
    }
}