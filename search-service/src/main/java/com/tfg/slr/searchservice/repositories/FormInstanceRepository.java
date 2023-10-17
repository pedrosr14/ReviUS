package com.tfg.slr.searchservice.repositories;

import com.tfg.slr.searchservice.models.FormInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FormInstanceRepository extends JpaRepository<FormInstance, Long> {

    Optional<FormInstance> findFormInstanceById(Long id);

    Optional<FormInstance> findFormInstanceByFormId(Long formId);
}