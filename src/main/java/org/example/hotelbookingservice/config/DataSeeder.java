package org.example.hotelbookingservice.config;

import lombok.RequiredArgsConstructor;
import org.example.hotelbookingservice.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.example.hotelbookingservice.entity.Role;

@Configuration
@RequiredArgsConstructor // Tự động inject RoleRepository
public class DataSeeder {

    private final RoleRepository roleRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // 1. Kiểm tra và tạo Role CUSTOMER
            // Dùng findByName để kiểm tra xem role "CUSTOMER" đã có chưa
            if (roleRepository.findByName("CUSTOMER").isEmpty()) {
                Role customerRole = new Role();
                customerRole.setId(1); // Giống như file Role.java của bạn, id không tự tăng
                customerRole.setName("CUSTOMER");
                roleRepository.save(customerRole);
            }

            // 2. Kiểm tra và tạo Role ADMIN
            // Dùng findByName để kiểm tra xem role "ADMIN" đã có chưa
            if (roleRepository.findByName("ADMIN").isEmpty()) {
                Role adminRole = new Role();
                adminRole.setId(2);
                adminRole.setName("ADMIN");
                roleRepository.save(adminRole);
            }
        };
    }
}