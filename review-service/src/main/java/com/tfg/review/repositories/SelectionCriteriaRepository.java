package com.tfg.review.repositories;

import com.tfg.review.models.SelectionCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SelectionCriteriaRepository extends JpaRepository<SelectionCriteria, Long> {

    Optional<SelectionCriteria> findSelectionCriteriaById(Long id);

    List<SelectionCriteria> findSelectionCriteriaByIdIn(List<Long> IdList);
}