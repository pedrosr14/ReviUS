package com.tfg.slr.usersmicroservice.repositories;

import com.tfg.slr.usersmicroservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserById(Long Id);

    Optional<User> findUserByEmail(String email);

    boolean existsById(Long id);

    boolean existsByEmail(String email);
}
