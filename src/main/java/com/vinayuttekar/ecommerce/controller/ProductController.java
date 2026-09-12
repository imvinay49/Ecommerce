package com.vinayuttekar.ecommerce.controller;

import com.vinayuttekar.ecommerce.dto.request.ProductRequest;
import com.vinayuttekar.ecommerce.dto.response.ApiResponse;
import com.vinayuttekar.ecommerce.dto.response.ProductPageResponse;
import com.vinayuttekar.ecommerce.dto.response.ProductResponse;
import com.vinayuttekar.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(@Valid @RequestBody ProductRequest productRequest, @PathVariable Long categoryId) {
        ProductResponse requestedProduct = productService.addProduct(productRequest, categoryId);

        ApiResponse<ProductResponse> response = new ApiResponse<>(
                true,
                "Product created successfully.",
                requestedProduct
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ApiResponse<ProductPageResponse>> getProducts(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size,
                                                                        @RequestParam(defaultValue = "productId") String sortBy,
                                                                        @RequestParam(defaultValue = "asc") String sortDir) {
        ProductPageResponse products = productService.getAllProducts(page, size, sortBy, sortDir);

        ApiResponse<ProductPageResponse> response = new ApiResponse<>(
                true,
                "Products fetched Successfully",
                products
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ApiResponse<ProductPageResponse>> getProductsByCategory(@PathVariable Long categoryId,@RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size,
                                                                                    @RequestParam(defaultValue = "productId") String sortBy,
                                                                                    @RequestParam(defaultValue = "asc") String sortDir) {

        ProductPageResponse productResponse =
                productService.searchByCategory(categoryId,page, size, sortBy, sortDir);

        ApiResponse<ProductPageResponse> response = new ApiResponse<>(
                true,
                "Successfully fetched products based on category.",
                productResponse
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ApiResponse<ProductPageResponse>> getProductByKeyword(@PathVariable String keyword,
                                                                                  @RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "10") int size,
                                                                                  @RequestParam(defaultValue = "productId") String sortBy,
                                                                                  @RequestParam(defaultValue = "asc") String sortDir){
        ProductPageResponse productResponse = productService.searchProductByKeyword(keyword,page, size, sortBy, sortDir);
        ApiResponse<ProductPageResponse> response = new ApiResponse<>(
                true,
                "Product fetched based on keyword",
                productResponse
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@Valid @RequestBody ProductRequest productRequest, @PathVariable Long productId){
        ProductResponse productResponse = productService.updateProduct(productRequest,productId);

        ApiResponse<ProductResponse> response= new ApiResponse<>(
                true,
                "Product updated Successfully",
                productResponse
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> deleteProduct(@PathVariable Long productId){
        ProductResponse productResponse = productService.deleteProduct(productId);

        ApiResponse<ProductResponse> response= new ApiResponse<>(
                true,
                "Product Deleted successfully",
                productResponse
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductImage(@PathVariable Long productId, @RequestParam("image")MultipartFile multipartFile) throws IOException {

        ProductResponse productResponse = productService.updateProductImage(productId,multipartFile);
        ApiResponse<ProductResponse> response = new ApiResponse<>(
                true,
                "Image updated Successfully",
                productResponse
        );

        return ResponseEntity.ok(response);
    }
}
