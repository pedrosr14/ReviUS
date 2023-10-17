package com.tfg.slr.searchservice.repositories;

import com.tfg.slr.searchservice.models.FormFieldInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormFieldInstanceRepository extends JpaRepository<FormFieldInstance, Long> {
}