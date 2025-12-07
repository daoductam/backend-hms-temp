package com.hms.PharmacyMS.util;

import org.apache.commons.codec.binary.Hex;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class MomoEncoder {

    public static String signHmacSHA256(String data, String secretKey) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}