package com.tfg.review.repositories;

import com.tfg.review.models.Protocol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProtocolRepository extends JpaRepository<Protocol, Long> {

  Optional<Protocol> findProtocolById(Long id);

}
