package com.vinayuttekar.ecommerce.service;

import com.vinayuttekar.ecommerce.dto.request.CategoryRequest;
import com.vinayuttekar.ecommerce.dto.response.CategoryResponse;
import com.vinayuttekar.ecommerce.entity.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);

    Page<CategoryResponse> getAllCategories(int page,
                                            int size,
                                            String direction,
                                            String sortBy);
    CategoryResponse getCategoryById(Long categoryId);
    CategoryResponse updateCategory(Long categoryId,
                                    CategoryRequest request);

    void deleteCategory(Long categoryId);
}
