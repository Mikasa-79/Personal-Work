package com.campushub.config;

import com.campushub.entity.User;
import com.campushub.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountInitializer implements CommandLineRunner {

    private static final String ADMIN_ACCOUNT = "admin";
    private static final String ADMIN_PASSWORD = "123456";

    private final UserRepository userRepository;

    public AdminAccountInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        User admin = userRepository.findByStudentNo(ADMIN_ACCOUNT)
                .orElseGet(User::new);

        admin.setStudentNo(ADMIN_ACCOUNT);
        admin.setPasswordHash(BCrypt.hashpw(ADMIN_PASSWORD, BCrypt.gensalt()));
        admin.setRole("ADMIN");
        admin.setStatus("ACTIVE");
        admin.setAdmin(true);
        admin.setNickname("Super Admin");
        admin.setCreditScore(admin.getCreditScore() != null ? admin.getCreditScore() : 100);

        userRepository.save(admin);
    }
}
