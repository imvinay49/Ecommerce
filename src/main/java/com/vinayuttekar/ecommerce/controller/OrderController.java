package com.vinayuttekar.ecommerce.controller;

import com.vinayuttekar.ecommerce.dto.request.OrderRequest;
import com.vinayuttekar.ecommerce.dto.response.OrderResponse;
import com.vinayuttekar.ecommerce.service.OrderService;
import com.vinayuttekar.ecommerce.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthUtil authUtil;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderResponse> orderProducts(@PathVariable String paymentMethod, @Valid @RequestBody OrderRequest orderRequest){
        String emailId = authUtil.loggedInEmail();
        OrderResponse orderResponse = orderService.placeOrder(
                emailId,
                orderRequest.getAddressId(),
                paymentMethod,
                orderRequest.getPgName(),
                orderRequest.getPgPaymentId(),
                orderRequest.getPgStatus(),
                orderRequest.getPgResponseMessage()
        );

        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }
}
