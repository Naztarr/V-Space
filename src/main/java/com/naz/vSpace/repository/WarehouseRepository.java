package com.naz.vSpace.repository;

import com.naz.vSpace.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
    Page<Warehouse> findByIsAvailableTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<Warehouse> findByOwner_Email(String email, Pageable pageable);

    List<Warehouse> findByDescriptionContainsIgnoreCase(String description);
}
