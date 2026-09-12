package com.vinayuttekar.ecommerce.controller;

import com.vinayuttekar.ecommerce.dto.response.CartResponse;
import com.vinayuttekar.ecommerce.entity.Cart;
import com.vinayuttekar.ecommerce.exception.ResourceNotFoundException;
import com.vinayuttekar.ecommerce.repository.CartRepository;
import com.vinayuttekar.ecommerce.service.CartService;
import com.vinayuttekar.ecommerce.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartRepository cartRepository;
    private final AuthUtil authUtil;

    @PostMapping("/carts/products/{productId}/{quantity}")
    public ResponseEntity<CartResponse> addProductToCart(@PathVariable Long productId,
                                                         @PathVariable Integer quantity){
        CartResponse cartResponse = cartService.addProductToCart(productId,quantity);
        return new ResponseEntity<CartResponse>(cartResponse, HttpStatus.CREATED);
    }

    @GetMapping("/admin/carts")
    public ResponseEntity<List<CartResponse>> getCarts(){
        List<CartResponse> cartResponses = cartService.getAllCarts();
        return new ResponseEntity<List<CartResponse>>(cartResponses,HttpStatus.OK);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartResponse> getCartById(){
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart == null) {
            throw new ResourceNotFoundException(
                    "Cart",
                    "email",
                    emailId
            );
        }
        Long cartId = cart.getCartId();
        CartResponse cartResponse = cartService.getCart(emailId,cartId);
        return new ResponseEntity<CartResponse>(cartResponse,HttpStatus.OK);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartResponse> updateCartProduct(@PathVariable Long productId,@PathVariable String operation){
        CartResponse cartResponse = cartService.updateProductQuantityInCart(productId,operation.equalsIgnoreCase("delete") ? -1 : 1);

        return new ResponseEntity<>(cartResponse,HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(
            @PathVariable Long cartId,
            @PathVariable Long productId) {

        String status =
                cartService.deleteProductFromCart(cartId, productId);

        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
