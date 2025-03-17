package com._hateam.repository;

import com._hateam.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Optional<Object> findByNameAndDeletedAtIsNull(String name);
}
