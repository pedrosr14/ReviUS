package com.tfg.slr.searchservice.repositories;

import com.tfg.slr.searchservice.models.Search;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {

    Optional<Search> findSearchById(Long id);

    Optional<Search> findSearchBySearchReference(String ref);
}