package com.vinayuttekar.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {

    private Long cartItemId;
    private CartResponse cart;
    private ProductResponse productDTO;
    private Integer quantity;
    private Double discount;
    private Double productPrice;
}
