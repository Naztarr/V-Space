package com.naz.vSpace.repository;

import com.naz.vSpace.entity.OwnerCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OwnerCredentialRepository extends JpaRepository<OwnerCredential, UUID> {
    OwnerCredential findByOwner_Id(UUID id);
}
