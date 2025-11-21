package org.example.hotelbookingservice.config;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hotelbookingservice.entity.*;
import org.example.hotelbookingservice.repository.HotelRepository;
import org.example.hotelbookingservice.repository.RoleRepository;
import org.example.hotelbookingservice.repository.UserRepository;
import org.example.hotelbookingservice.repository.UserroleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.LinkedHashSet;

@Configuration
@RequiredArgsConstructor
@Slf4j// Tự động inject RoleRepository
public class DataSeeder {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserroleRepository userroleRepository;
    private final PasswordEncoder passwordEncoder;
    private final HotelRepository hotelRepository;

    @Bean
    @Transactional
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


            Role customerRole = createRoleIfNotFound("CUSTOMER", 1);
            Role adminRole = createRoleIfNotFound("ADMIN", 2);

            // 2. Create Default Accounts
            createAccountIfNotFound(
                    "admin@gmail.com",
                    "Admin User",
                    "1234567890",
                    "admin123",
                    adminRole
            );

            createAccountIfNotFound(
                    "customer@gmail.com",
                    "Customer User",
                    "0987654321",
                    "customer123",
                    customerRole
            );

            //Create hotel for admin
            createHotelForAdminIfNotFound("admin@gmail.com");


        };

    }

    private void createAccountIfNotFound(String email, String fullName, String phone, String password, Role role) {
        // Check if user already exists
        if (userRepository.findByEmail(email).isEmpty()) {
            // 1. Create and save the User
            User user = User.builder()
                    .fullName(fullName)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .phone(phone)
                    .dob(LocalDate.now().minusYears(25)) // Example DOB
                    .activate(Boolean.TRUE)
                    .userRoles(new LinkedHashSet<>()) // Initialize the set
                    .build();

            User savedUser = userRepository.save(user);

            // 2. Create the User-Role link
            UserroleId userroleId = new UserroleId();
            userroleId.setUserId(savedUser.getId());
            userroleId.setRoleId(role.getId());

            Userrole userrole = new Userrole();
            userrole.setId(userroleId);
            userrole.setUser(savedUser);
            userrole.setRole(role);

            userroleRepository.save(userrole);

            log.info("Created default account: {}", email);
        }
    }

    private Role createRoleIfNotFound(String roleName, int id) {
        return roleRepository.findByName(roleName).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setId(id); // Using manual ID as per your Role entity
            newRole.setName(roleName);
            log.info("Creating role: {}", roleName);
            return roleRepository.save(newRole);
        });
    }


    private void createHotelForAdminIfNotFound(String adminEmail) {
        userRepository.findByEmail(adminEmail).ifPresent(admin -> {
            // Kiểm tra xem admin đã có hotel nào chưa (hoặc check hotelRepository trống)
            if (hotelRepository.count() == 0) {
                Hotel hotel = new Hotel();
                hotel.setName("Luxury Hotel");
                hotel.setLocation("Ho Chi Minh City");
                hotel.setDescription("A 5-star luxury hotel in the heart of the city.");
                hotel.setStarRating(5);
                hotel.setEmail("contact@example.com");
                hotel.setPhone("0909000999");
                hotel.setContactName("Manager");
                hotel.setContactPhone("0909000888");
                hotel.setIsActive(true);
                hotel.setUser(admin); // Gán Admin làm chủ khách sạn

                hotelRepository.save(hotel);
                log.info("Created default Hotel for Admin: {}", adminEmail);
            }
        });
    }
}