package com._hateam.repository;

import com._hateam.feign.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Object> findByNameAndDeletedAtIsNull(String name);

    Page<Company> findByHubId(UUID hubId, Pageable pageable);

}
