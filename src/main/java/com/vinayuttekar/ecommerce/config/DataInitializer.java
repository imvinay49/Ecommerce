package com.vinayuttekar.ecommerce.config;

import com.vinayuttekar.ecommerce.entity.AppRole;
import com.vinayuttekar.ecommerce.entity.Role;
import com.vinayuttekar.ecommerce.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.vinayuttekar.ecommerce.entity.User;
import com.vinayuttekar.ecommerce.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Override
    public void run(String... args) {

        createRoleIfNotExists(AppRole.ROLE_USER);
        createRoleIfNotExists(AppRole.ROLE_SELLER);
        createRoleIfNotExists(AppRole.ROLE_ADMIN);
        createInitialAdmin();
    }

    private void createRoleIfNotExists(AppRole roleName) {

        if (roleRepository.findByRoleName(roleName).isEmpty()) {

            Role role = new Role();
            role.setRoleName(roleName);

            roleRepository.save(role);
        }
    }

    private void createInitialAdmin() {

        if (userRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }

        Role adminRole = roleRepository
                .findByRoleName(AppRole.ROLE_ADMIN)
                .orElseThrow(() ->
                        new RuntimeException("ROLE_ADMIN not found")
                );

        User admin = new User();

        admin.setUserName("admin");
        admin.setEmail(adminEmail);

        admin.setPassword(
                passwordEncoder.encode(adminPassword)
        );

        admin.getRoles().add(adminRole);

        userRepository.save(admin);
    }
}