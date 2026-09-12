package com.vinayuttekar.ecommerce.service.impl;

import com.vinayuttekar.ecommerce.dto.request.ProductRequest;
import com.vinayuttekar.ecommerce.dto.response.ProductPageResponse;
import com.vinayuttekar.ecommerce.dto.response.ProductResponse;
import com.vinayuttekar.ecommerce.entity.Cart;
import com.vinayuttekar.ecommerce.entity.CartItem;
import com.vinayuttekar.ecommerce.entity.Category;
import com.vinayuttekar.ecommerce.entity.Product;
import com.vinayuttekar.ecommerce.exception.ResourceAlreadyExistsException;
import com.vinayuttekar.ecommerce.exception.ResourceNotFoundException;
import com.vinayuttekar.ecommerce.repository.CartItemRepository;
import com.vinayuttekar.ecommerce.repository.CartRepository;
import com.vinayuttekar.ecommerce.repository.CategoryRepository;
import com.vinayuttekar.ecommerce.repository.OrderItemRepository;
import com.vinayuttekar.ecommerce.repository.ProductRepository;
import com.vinayuttekar.ecommerce.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final com.vinayuttekar.ecommerce.service.FileService fileService;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;

    @Value("${project.image:images}")
    private String path;

    @Override
    public ProductResponse addProduct(ProductRequest request, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        String name = request.getProductName().trim();
        if (productRepository.existsByProductNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistsException("Product", "name", name);
        }
        Product product = modelMapper.map(request, Product.class);
        product.setProductName(name);
        product.setCategory(category);
        product.setSpecialPrice(calculateSpecialPrice(product.getPrice(), product.getDiscount()));
        return modelMapper.map(productRepository.save(product), ProductResponse.class);
    }

    @Override
    public ProductPageResponse getAllProducts(int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(validatePage(page), validateSize(size), buildSort(sortBy, sortDir));
        return getProductPageResponse(productRepository.findAll(pageable));
    }

    @Override
    public ProductPageResponse searchByCategory(Long categoryId, int page, int size, String sortBy, String sortDir) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        Pageable pageable = PageRequest.of(validatePage(page), validateSize(size), buildSort(sortBy, sortDir));
        return getProductPageResponse(productRepository.findByCategory(category, pageable));
    }

    @Override
    public ProductPageResponse searchProductByKeyword(String keyword, int page, int size, String sortBy, String sortDir) {
        if (keyword == null || keyword.isBlank()) throw new IllegalArgumentException("Keyword is required");
        Pageable pageable = PageRequest.of(validatePage(page), validateSize(size), buildSort(sortBy, sortDir));
        return getProductPageResponse(productRepository.findByProductNameContainingIgnoreCase(keyword.trim(), pageable));
    }

    @Override
    public ProductResponse updateProduct(ProductRequest request, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        String name = request.getProductName().trim();
        if (!product.getProductName().equalsIgnoreCase(name) && productRepository.existsByProductNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistsException("Product", "name", name);
        }
        product.setProductName(name);
        product.setDescription(request.getDescription());
        product.setQuantity(request.getQuantity());
        product.setDiscount(request.getDiscount());
        product.setPrice(request.getPrice());
        product.setSpecialPrice(calculateSpecialPrice(request.getPrice(), request.getDiscount()));
        return modelMapper.map(productRepository.save(product), ProductResponse.class);
    }

    @Override
    public ProductResponse deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        if (orderItemRepository.existsByProductProductId(productId)) {
            throw new IllegalStateException("Cannot delete a product that is part of an existing order");
        }

        List<CartItem> items = cartItemRepository.findCartItemsByProductId(productId);
        for (CartItem item : List.copyOf(items)) {
            Cart cart = item.getCart();
            if (cart != null) {
                cart.getCartItems().remove(item);
                recalculateCartTotal(cart);
                cartRepository.save(cart);
            }
            cartItemRepository.delete(item);
        }
        ProductResponse response = modelMapper.map(product, ProductResponse.class);
        productRepository.delete(product);
        return response;
    }

    @Override
    public ProductResponse updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        String fileName = fileService.uploadImage(path, image);
        product.setImage(fileName);
        return modelMapper.map(productRepository.save(product), ProductResponse.class);
    }

    private Double calculateSpecialPrice(Double price, Double discount) {
        if (discount == null) return price;
        return Math.round((price - (price * discount / 100)) * 100.0) / 100.0;
    }

    private void recalculateCartTotal(Cart cart) {
        double total = cart.getCartItems().stream().mapToDouble(i -> i.getProductPrice() * i.getQuantity()).sum();
        cart.setTotalPrice(Math.round(total * 100.0) / 100.0);
    }

    private Sort buildSort(String sortBy, String sortDir) {
        String field = switch (sortBy) {
            case "productId", "productName", "price", "specialPrice", "discount", "quantity" -> sortBy;
            default -> throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        };
        return "desc".equalsIgnoreCase(sortDir) ? Sort.by(field).descending() : Sort.by(field).ascending();
    }

    private int validatePage(int page) { if (page < 0) throw new IllegalArgumentException("Page must be 0 or greater"); return page; }
    private int validateSize(int size) { if (size < 1 || size > 100) throw new IllegalArgumentException("Page size must be between 1 and 100"); return size; }
    private ProductPageResponse getProductPageResponse(Page<Product> page) {
        ProductPageResponse response = new ProductPageResponse();
        response.setContent(page.getContent().stream().map(p -> modelMapper.map(p, ProductResponse.class)).toList());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLast(page.isLast());
        return response;
    }
}
