package com.vinayuttekar.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private String email;
    private List<OrderItemResponse> orderItems = new ArrayList<>();
    private LocalDate orderDate;
    private PaymentResponse payment;
    private Double totalAmount;
    private String orderStatus;
    private Long addressId;
}
