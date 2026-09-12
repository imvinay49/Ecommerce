package com.vinayuttekar.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {

    private Long orderItemId;
    private ProductResponse productResponse;
    private Integer quantity;
    private double discount;
    private double orderedProductPrice;
}
