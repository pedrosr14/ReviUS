package com.tfg.review.repositories;

import com.tfg.review.models.FormField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface FormFieldRepository extends JpaRepository<FormField, Long> {


    boolean existsFormFieldById(Long id);

    Optional<FormField> findFormFieldById(Long id);
}