package com.naz.vSpace.repository;

import com.naz.vSpace.entity.House;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HouseRepository extends JpaRepository<House, UUID> {

    Page<House> findByIsAvailableTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<House> findByOwner_Email(String email, Pageable pageable);

    List<House> findByDescriptionContainsIgnoreCase(String description);
}
