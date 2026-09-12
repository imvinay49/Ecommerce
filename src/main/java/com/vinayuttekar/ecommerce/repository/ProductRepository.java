package com.vinayuttekar.ecommerce.repository;

import com.vinayuttekar.ecommerce.entity.Category;
import com.vinayuttekar.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByProductNameIgnoreCase(String name);
    List<Product> findByCategoryOrderByPriceAsc(Category category);
    Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Product> findByCategory(Category category, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.productId = :productId")
    java.util.Optional<Product> findByIdForUpdate(@Param("productId") Long productId);
}
