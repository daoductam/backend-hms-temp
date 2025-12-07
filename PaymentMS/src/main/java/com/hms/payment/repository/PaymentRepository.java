package com.hms.payment.repository;

import com.hms.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Spring Data JPA sẽ tự động parse tên hàm này thành câu query:
    // SELECT * FROM payment WHERE sale_id = ? ORDER BY id DESC LIMIT 1
    Optional<Payment> findTopBySaleIdOrderByIdDesc(Long saleId);
}
