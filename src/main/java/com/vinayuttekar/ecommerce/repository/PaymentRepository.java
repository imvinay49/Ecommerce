package com.vinayuttekar.ecommerce.repository;

import com.vinayuttekar.ecommerce.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
