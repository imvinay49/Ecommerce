package com.vinayuttekar.ecommerce.service.impl;

import com.vinayuttekar.ecommerce.dto.response.OrderItemResponse;
import com.vinayuttekar.ecommerce.dto.response.OrderResponse;
import com.vinayuttekar.ecommerce.dto.response.ProductResponse;
import com.vinayuttekar.ecommerce.entity.*;
import com.vinayuttekar.ecommerce.exception.OutOfStockException;
import com.vinayuttekar.ecommerce.exception.ResourceNotFoundException;
import com.vinayuttekar.ecommerce.repository.*;
import com.vinayuttekar.ecommerce.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderResponse placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("Payment method is required");
        }

        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart == null || cart.getCartItems().isEmpty()) throw new ResourceNotFoundException("Cart is empty");

        Address address = addressRepository.findById(addressId)
                .filter(a -> a.getUser() != null && a.getUser().getEmail().equalsIgnoreCase(emailId))
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        // Re-check stock at checkout; stock may have changed after the item was added to the cart.
        // Lock every product row while checking/decreasing stock so two checkouts cannot oversell it.
        for (CartItem item : cart.getCartItems()) {
            Product product = productRepository.findByIdForUpdate(item.getProduct().getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", item.getProduct().getProductId()));
            int available = product.getQuantity() == null ? 0 : product.getQuantity();
            if (available < item.getQuantity()) {
                throw new OutOfStockException("Only " + available + " items are available for " + product.getProductName());
            }
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0.0;
        for (CartItem cartItem : cart.getCartItems()) {
            double unitPrice = cartItem.getProductPrice();
            total += unitPrice * cartItem.getQuantity();
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(unitPrice);
            orderItems.add(orderItem);
        }
        total = Math.round(total * 100.0) / 100.0;

        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(total);
        order.setOrderStatus("Order Accepted");
        order.setAddress(address);

        Payment payment = new Payment(paymentMethod.trim().toUpperCase(), pgPaymentId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);
        Order savedOrder = orderRepository.save(order);

        for (OrderItem item : orderItems) item.setOrder(savedOrder);
        List<OrderItem> savedItems = orderItemRepository.saveAll(orderItems);

        for (CartItem cartItem : new ArrayList<>(cart.getCartItems())) {
            Product product = productRepository.findByIdForUpdate(cartItem.getProduct().getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", cartItem.getProduct().getProductId()));
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
            cart.getCartItems().remove(cartItem);
        }
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);

        OrderResponse response = modelMapper.map(savedOrder, OrderResponse.class);
        response.setOrderItems(new ArrayList<>());
        for (OrderItem item : savedItems) {
            OrderItemResponse itemResponse = modelMapper.map(item, OrderItemResponse.class);
            ProductResponse productResponse = modelMapper.map(item.getProduct(), ProductResponse.class);
            // Response should show the purchased quantity, not current stock.
            productResponse.setQuantity(item.getQuantity());
            itemResponse.setProductResponse(productResponse);
            response.getOrderItems().add(itemResponse);
        }
        response.setAddressId(addressId);
        return response;
    }
}
