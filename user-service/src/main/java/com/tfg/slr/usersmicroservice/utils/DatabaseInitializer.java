package com.tfg.slr.usersmicroservice.utils;

import com.tfg.slr.usersmicroservice.models.UserAccount;
import com.tfg.slr.usersmicroservice.repositories.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * This class initializes the database with a default admin user account created
 * If the app is launched with the admin already created, nothing is created nor deleted
 * Password is encoded
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {
    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        if (!userAccountRepository.existsByUserName(adminUsername)) {

            UserAccount adminUserAccount = new UserAccount();
            adminUserAccount.setUserName(adminUsername);
            adminUserAccount.setPassword(passwordEncoder.encode(adminPassword));
            adminUserAccount.setIsAdmin(true);

            userAccountRepository.save(adminUserAccount);
        }
    }
}

