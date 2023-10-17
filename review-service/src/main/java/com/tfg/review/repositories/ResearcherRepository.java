package com.tfg.review.repositories;

import com.tfg.review.models.Researcher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface ResearcherRepository extends JpaRepository<Researcher, Long> {
    Optional<Researcher> findResearcherById(Long id);

    Collection<Researcher> findAllByUserId(Long userId);
}
