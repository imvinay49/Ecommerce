package com.vinayuttekar.ecommerce.service.impl;

import com.vinayuttekar.ecommerce.dto.response.CartResponse;
import com.vinayuttekar.ecommerce.dto.response.ProductResponse;
import com.vinayuttekar.ecommerce.dto.response.UserResponse;
import com.vinayuttekar.ecommerce.entity.Cart;
import com.vinayuttekar.ecommerce.entity.CartItem;
import com.vinayuttekar.ecommerce.entity.Product;
import com.vinayuttekar.ecommerce.exception.OutOfStockException;
import com.vinayuttekar.ecommerce.exception.ResourceAlreadyExistsException;
import com.vinayuttekar.ecommerce.exception.ResourceNotFoundException;
import com.vinayuttekar.ecommerce.repository.CartItemRepository;
import com.vinayuttekar.ecommerce.repository.CartRepository;
import com.vinayuttekar.ecommerce.repository.ProductRepository;
import com.vinayuttekar.ecommerce.service.CartService;
import com.vinayuttekar.ecommerce.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;
    private final AuthUtil authUtil;

    @Override
    public CartResponse addProductToCart(Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        Cart cart = createCart();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if (cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId) != null) {
            throw new ResourceAlreadyExistsException("Product", "productId", productId);
        }
        validateStock(product, quantity);

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setCart(cart);
        item.setQuantity(quantity);
        item.setDiscount(product.getDiscount() == null ? 0.0 : product.getDiscount());
        item.setProductPrice(product.getSpecialPrice() == null ? product.getPrice() : product.getSpecialPrice());
        cart.getCartItems().add(item);
        cartItemRepository.save(item);
        recalculateCartTotal(cart);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Override
    public List<CartResponse> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if (carts.isEmpty()) throw new ResourceNotFoundException("No carts found");
        return carts.stream().map(this::toResponseWithUser).toList();
    }

    @Override
    public CartResponse getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null) throw new ResourceNotFoundException("Cart", "cartId", cartId);
        return toResponse(cart);
    }

    @Override
    public CartResponse updateProductQuantityInCart(Long productId, Integer quantityChange) {
        if (quantityChange == null || (quantityChange != 1 && quantityChange != -1)) {
            throw new IllegalArgumentException("Quantity operation must be 1 or -1");
        }
        Cart cart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (cart == null) throw new ResourceNotFoundException("Cart", "email", authUtil.loggedInEmail());

        CartItem item = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if (item == null) throw new ResourceNotFoundException("Product", "productId", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        int newQuantity = item.getQuantity() + quantityChange;
        if (newQuantity < 1) {
            cart.getCartItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            validateStock(product, newQuantity);
            item.setQuantity(newQuantity);
            item.setProductPrice(product.getSpecialPrice() == null ? product.getPrice() : product.getSpecialPrice());
            item.setDiscount(product.getDiscount() == null ? 0.0 : product.getDiscount());
            cartItemRepository.save(item);
        }
        recalculateCartTotal(cart);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(authUtil.loggedInEmail(), cartId);
        if (cart == null) throw new ResourceNotFoundException("Cart", "cartId", cartId);
        CartItem item = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (item == null) throw new ResourceNotFoundException("Product", "productId", productId);
        String productName = item.getProduct().getProductName();
        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);
        recalculateCartTotal(cart);
        cartRepository.save(cart);
        return "Product " + productName + " removed from Cart";
    }

    private void validateStock(Product product, int quantity) {
        int available = product.getQuantity() == null ? 0 : product.getQuantity();
        if (available <= 0) throw new OutOfStockException("Product is out of stock");
        if (available < quantity) throw new OutOfStockException("Only " + available + " items are available");
    }

    private void recalculateCartTotal(Cart cart) {
        double total = cart.getCartItems().stream()
                .mapToDouble(item -> item.getProductPrice() * item.getQuantity()).sum();
        cart.setTotalPrice(Math.round(total * 100.0) / 100.0);
    }

    private CartResponse toResponse(Cart cart) {
        CartResponse response = modelMapper.map(cart, CartResponse.class);
        response.setProducts(cart.getCartItems().stream().map(item -> {
            ProductResponse product = modelMapper.map(item.getProduct(), ProductResponse.class);
            product.setQuantity(item.getQuantity());
            return product;
        }).toList());
        return response;
    }

    private CartResponse toResponseWithUser(Cart cart) {
        CartResponse response = toResponse(cart);
        response.setUser(modelMapper.map(cart.getUser(), UserResponse.class));
        return response;
    }

    private Cart createCart() {
        Cart existing = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (existing != null) return existing;
        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());
        return cartRepository.save(cart);
    }
}
