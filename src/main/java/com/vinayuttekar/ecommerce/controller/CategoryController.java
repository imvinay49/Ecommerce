package com.vinayuttekar.ecommerce.controller;
import com.vinayuttekar.ecommerce.dto.request.CategoryRequest;
import com.vinayuttekar.ecommerce.dto.response.ApiResponse;
import com.vinayuttekar.ecommerce.dto.response.CategoryResponse;
import com.vinayuttekar.ecommerce.entity.Category;
import com.vinayuttekar.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request){
        CategoryResponse categoryResponse = categoryService.createCategory(request);

        ApiResponse<CategoryResponse> response = new ApiResponse<>(
                true,
                "Category created successfully.",
                categoryResponse
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getAllCategories(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "asc") String direction, @RequestParam(defaultValue = "categoryId") String sortBy ){
        Page<CategoryResponse> categories = categoryService.getAllCategories(page,size,direction,sortBy);

        ApiResponse<Page<CategoryResponse>> response = new ApiResponse<>(
                true,
                "Categories fetched successfully.",
                categories
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long categoryId){
        CategoryResponse category = categoryService.getCategoryById(categoryId);
        ApiResponse<CategoryResponse> response = new ApiResponse<>(
                true,
                "Category fetched successfully.",
                category
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryRequest request){
        CategoryResponse category = categoryService.updateCategory(categoryId,request);

        ApiResponse<CategoryResponse> response = new ApiResponse<>(
                true,
                "Category Updated Successfully",
                category
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long categoryId){

        categoryService.deleteCategory(categoryId);
        ApiResponse<Void> response = new ApiResponse<>(
                true,
                "Category Deleted Successfully",
                null
        );

        return ResponseEntity.ok(response);
    }
}
