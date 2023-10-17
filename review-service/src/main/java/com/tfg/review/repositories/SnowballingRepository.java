package com.tfg.review.repositories;

import com.tfg.review.models.Snowballing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SnowballingRepository extends JpaRepository<Snowballing, Long> {

    @Override
    boolean existsById(Long id);

    Optional<Snowballing> findSnowballingById(Long id);
}