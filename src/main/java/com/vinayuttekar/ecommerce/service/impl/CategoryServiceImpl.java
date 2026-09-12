package com.vinayuttekar.ecommerce.service.impl;

import com.vinayuttekar.ecommerce.dto.request.CategoryRequest;
import com.vinayuttekar.ecommerce.dto.response.CategoryResponse;
import com.vinayuttekar.ecommerce.entity.Category;
import com.vinayuttekar.ecommerce.exception.ResourceAlreadyExistsException;
import com.vinayuttekar.ecommerce.exception.ResourceNotFoundException;
import com.vinayuttekar.ecommerce.repository.CategoryRepository;
import com.vinayuttekar.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        String name = request.getName().trim();
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistsException("Category", "name", name);
        }
        Category saved = categoryRepository.save(modelMapper.map(request, Category.class));
        return modelMapper.map(saved, CategoryResponse.class);
    }

    @Override
    public Page<CategoryResponse> getAllCategories(int page, int size, String direction, String sortBy) {
        Pageable pageable = PageRequest.of(validatePage(page), validateSize(size), buildSort(direction, sortBy, "categoryId", "name"));
        return categoryRepository.findAll(pageable).map(c -> modelMapper.map(c, CategoryResponse.class));
    }

    @Override
    public CategoryResponse getCategoryById(Long categoryId) {
        return modelMapper.map(findCategory(categoryId), CategoryResponse.class);
    }

    @Override
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) {
        Category category = findCategory(categoryId);
        String name = request.getName().trim();
        if (!category.getName().equalsIgnoreCase(name) && categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistsException("Category", "name", name);
        }
        category.setName(name);
        return modelMapper.map(categoryRepository.save(category), CategoryResponse.class);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = findCategory(categoryId);
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new IllegalStateException("Cannot delete a category that contains products");
        }
        categoryRepository.delete(category);
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    static Sort buildSort(String direction, String sortBy, String... allowed) {
        String field = java.util.Arrays.stream(allowed).filter(a -> a.equals(sortBy)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid sort field: " + sortBy));
        return "desc".equalsIgnoreCase(direction) ? Sort.by(field).descending() : Sort.by(field).ascending();
    }
    static int validatePage(int page) {
        if (page < 0) throw new IllegalArgumentException("Page must be 0 or greater");
        return page;
    }
    static int validateSize(int size) {
        if (size < 1 || size > 100) throw new IllegalArgumentException("Page size must be between 1 and 100");
        return size;
    }
}
