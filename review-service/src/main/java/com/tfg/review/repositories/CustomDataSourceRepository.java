package com.tfg.review.repositories;

import com.tfg.review.models.CustomDataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CustomDataSourceRepository extends JpaRepository<CustomDataSource, Long> {

    @Override
    boolean existsById(Long id);

    Optional<CustomDataSource> findCustomDataSourceById(Long id);
}