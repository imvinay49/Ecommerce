package com.vinayuttekar.ecommerce.service;

import com.vinayuttekar.ecommerce.dto.request.ProductRequest;
import com.vinayuttekar.ecommerce.dto.response.ProductPageResponse;
import com.vinayuttekar.ecommerce.dto.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductResponse addProduct(ProductRequest productRequest, Long categoryId);

//    List<ProductResponse> getAllProducts();
    ProductPageResponse getAllProducts(
            int page,
            int size,
            String sortBy,
            String sortDir
    );

    ProductPageResponse searchByCategory(Long categoryId,int page,
                                         int size,
                                         String sortBy,
                                         String sortDir);

    ProductPageResponse searchProductByKeyword(String keyword,int page,
                                               int size,
                                               String sortBy,
                                               String sortDir);

    ProductResponse updateProduct(ProductRequest productRequest, Long productId);

    ProductResponse deleteProduct(Long productId);

    ProductResponse updateProductImage(Long productId, MultipartFile multipartFile) throws IOException;
}
