package com.tfg.review.repositories;

import com.tfg.review.models.DataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, Long> {

    @Override
    boolean existsById(Long id);

    Optional<DataSource> findDataSourceById(Long id);
}