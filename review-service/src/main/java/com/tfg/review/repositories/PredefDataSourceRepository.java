package com.tfg.review.repositories;

import com.tfg.review.models.PredefDataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PredefDataSourceRepository extends JpaRepository<PredefDataSource, Long> {

    @Override
    boolean existsById(Long id);

    Optional<PredefDataSource> findPredefDataSourceById(Long id);
}