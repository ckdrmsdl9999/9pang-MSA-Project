package com._hateam.repository;

import com._hateam.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Object> findByNameAndDeletedAtIsNull(String name);
}
