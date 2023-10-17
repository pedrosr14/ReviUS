package com.tfg.review.repositories;

import com.tfg.review.models.SLR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SLRRepository extends JpaRepository<SLR, Long> {

    @Override
    boolean existsById(Long id);

    Optional<SLR> findSLRById(Long slrId);

    void deleteSLRById(Long slrId);
}
