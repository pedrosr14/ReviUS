package com.tfg.slr.usersmicroservice.repositories;

import com.tfg.slr.usersmicroservice.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUserName(String userName);

    Optional<UserAccount> findUserAccountById(Long id);

    boolean existsByUserName (String userName);
}
