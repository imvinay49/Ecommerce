package com.vinayuttekar.ecommerce.service;

import com.vinayuttekar.ecommerce.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);
}
