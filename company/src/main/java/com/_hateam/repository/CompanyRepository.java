package com._hateam.repository;

import com._hateam.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Object> findByNameAndDeletedAtIsNull(String name);

    Page<Company> findByHubIdAndDeletedAtIsNull(UUID hubId, Pageable pageable);

    List<Company> findByHubIdAndDeletedAtIsNull(UUID hubId);

    Company findByIdAndHubIdAndDeletedAtIsNull(UUID id, UUID hubId);
}
