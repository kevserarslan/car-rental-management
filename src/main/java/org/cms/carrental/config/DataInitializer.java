package org.cms.carrental.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cms.carrental.entity.User;
import org.cms.carrental.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Uygulama başladığında varsayılan admin kullanıcısı oluşturur.
 *
 * Admin bilgileri:
 * Email: admin@carrental.com
 * Password: admin123
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createDefaultAdmin();
    }

    private void createDefaultAdmin() {
        String adminEmail = "admin@carrental.com";

        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhone("05551111111");
            admin.setAddress("Admin Office");
            admin.setRole(User.Role.ADMIN);

            userRepository.save(admin);
            log.info("✅ Default admin user created: {}", adminEmail);
            log.info("📧 Email: admin@carrental.com");
            log.info("🔑 Password: admin123");
        } else {
            log.info("ℹ️ Admin user already exists: {}", adminEmail);
        }
    }
}

