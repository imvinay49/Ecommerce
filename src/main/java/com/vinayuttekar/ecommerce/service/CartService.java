package com.vinayuttekar.ecommerce.service;

import com.vinayuttekar.ecommerce.dto.response.CartResponse;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
    public CartResponse addProductToCart(Long productId, Integer quantity);

    List<CartResponse> getAllCarts();

    CartResponse getCart(String emailId, Long cartId);


    @Transactional
    CartResponse updateProductQuantityInCart(Long productId, Integer quantity);

    String deleteProductFromCart(Long cartId, Long productId);
}
