package com.tfg.review.repositories;

import com.tfg.review.models.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface FormRepository extends JpaRepository<Form, Long> {

    boolean existsById(Long id);

    Optional<Form> findFormById(Long id);

}