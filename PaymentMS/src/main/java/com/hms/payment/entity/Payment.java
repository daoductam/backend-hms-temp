package com.hms.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long saleId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // momo / vnpay / chuyenkhoan
    @Column(length = 50, nullable = false)
    private String provider;

    // transId từ gateway thanh toán
    @Column(length = 100)
    private String transId;

    // resultCode từ Momo / VNPay
    private Integer resultCode;

//    private LocalDateTime createdAt;
//
//    private LocalDateTime updatedAt;
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//        updatedAt = createdAt;
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
}
