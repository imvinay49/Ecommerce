package com.vinayuttekar.ecommerce.repository;

import com.vinayuttekar.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
