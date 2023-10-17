package com.tfg.slr.searchservice.repositories;

import com.tfg.slr.searchservice.models.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    Optional<Study> findStudyById(Long id);
}