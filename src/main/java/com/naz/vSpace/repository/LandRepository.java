package com.naz.vSpace.repository;

import com.naz.vSpace.entity.Land;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LandRepository extends JpaRepository<Land, UUID> {
    Page<Land> findByIsAvailableTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<Land> findByOwner_Email(String email, Pageable pageable);

    List<Land> findByDescriptionContainsIgnoreCase(String description);
}
