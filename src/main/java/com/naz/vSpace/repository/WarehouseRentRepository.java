package com.naz.vSpace.repository;

import com.naz.vSpace.entity.WarehouseRent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WarehouseRentRepository extends JpaRepository<WarehouseRent, UUID> {
}
