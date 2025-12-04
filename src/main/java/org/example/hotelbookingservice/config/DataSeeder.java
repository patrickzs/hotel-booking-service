package org.example.hotelbookingservice.config;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hotelbookingservice.entity.*;
import org.example.hotelbookingservice.enums.RoomType;
import org.example.hotelbookingservice.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j// Tự động inject RoleRepository
public class DataSeeder {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserroleRepository userroleRepository;
    private final PasswordEncoder passwordEncoder;
    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final HotelamenityRepository hotelamenityRepository;
    private final RoomRepository roomRepository;
    private final RoomamenityRepository roomamenityRepository;

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

            List<Amenity> hotelAmenities = createHotelAmenities();
            List<Amenity> roomAmenities = createRoomAmenities();

            assignAmenitiesToHotel("Luxury Hotel", hotelAmenities);
            createRoomAndAssignAmenities("Luxury Hotel", roomAmenities);


        };

    }

    private List<Amenity> createRoomAmenities() {
        List<Amenity> list = new ArrayList<>();
        String[] names = {"Air Conditioning", "Flat-screen TV", "Minibar", "Balcony", "Private Bathroom", "Hairdryer", "Coffee Machine"};

        for (String name : names) {
            Amenity amenity = amenityRepository.findByName(name).orElseGet(() -> {
                Amenity newAmenity = new Amenity();
                newAmenity.setName(name);
                newAmenity.setType("Room Feature"); // Loại tiện ích phòng
                return amenityRepository.save(newAmenity);
            });
            list.add(amenity);
        }
        return list;
    }

    private List<Amenity> createHotelAmenities() {
        List<Amenity> list = new ArrayList<>();
        String[] names = {"Free Wi-Fi", "Swimming Pool", "Fitness Center", "Spa & Wellness", "Parking", "Restaurant", "Bar"};

        for (String name : names) {
            Amenity amenity = amenityRepository.findByName(name).orElseGet(() -> {
                Amenity newAmenity = new Amenity();
                newAmenity.setName(name);
                newAmenity.setType("Hotel Service");
                return amenityRepository.save(newAmenity);
            });
            list.add(amenity);
        }
        return list;
    }

    private void createRoomAndAssignAmenities(String hotelName, List<Amenity> amenities) {
        hotelRepository.findAll().stream()
                .filter(h -> h.getName().equals(hotelName))
                .findFirst()
                .ifPresent(hotel -> {
                    if (roomRepository.findAvailableRoomsByHotelId(hotel.getId(), LocalDate.now(), LocalDate.now().plusDays(1)).isEmpty()) {
                        Room room1 = createRoom(hotel, 101, "Deluxe Single Room", RoomType.SINGLE, new BigDecimal("500000"), 1);
                        linkAmenitiesToRoom(room1, amenities);

                        Room room2 = createRoom(hotel, 102, "Premier Double Room", RoomType.DOUBLE, new BigDecimal("800000"), 2);
                        linkAmenitiesToRoom(room2, amenities);

                        log.info("Created default rooms and assigned amenities for hotel '{}'", hotelName);
                    }
                });
    }

    private void assignAmenitiesToHotel(String hotelName, List<Amenity> amenities) {

        hotelRepository.findAll().stream()
                .filter(h -> h.getName().equals(hotelName))
                .findFirst()
                .ifPresent(hotel -> {
                    for (Amenity amenity : amenities) {
                        HotelamenityId id = new HotelamenityId();
                        id.setHotelId(hotel.getId());
                        id.setAmenityId(amenity.getId());

                        if (!hotelamenityRepository.existsById(id)) {
                            Hotelamenity hotelamenity = new Hotelamenity();
                            hotelamenity.setId(id);
                            hotelamenity.setHotel(hotel);
                            hotelamenity.setAmenity(amenity);
                            hotelamenityRepository.save(hotelamenity);
                        }
                    }
                    log.info("Assigned {} amenities to hotel '{}'", amenities.size(), hotelName);
                });
    }

    private Room createRoom(Hotel hotel, Integer number, String name, RoomType type, BigDecimal price, Integer capacity) {
        if (roomRepository.existsByRoomNumberAndHotelId(number, hotel.getId())) {
            return null;
        }

        Room room = new Room();
        room.setRoomNumber(number);
        room.setName(name);
        room.setType(type);
        room.setPrice(price);
        room.setCapacity(capacity);
        room.setAmount(1);
        room.setDescription("A beautiful room with city view.");
        room.setHotel(hotel);

        return roomRepository.save(room);
    }


    private void linkAmenitiesToRoom(Room room, List<Amenity> amenities) {
        if (room == null) return;

        for (Amenity amenity : amenities) {
            RoomamenityId id = new RoomamenityId();
            id.setRoomId(room.getId());
            id.setAmenityId(amenity.getId());

            if (!roomamenityRepository.existsById(id)) {
                Roomamenity roomamenity = new Roomamenity();
                roomamenity.setId(id);
                roomamenity.setRoom(room);
                roomamenity.setAmenity(amenity);
                roomamenityRepository.save(roomamenity);
            }
        }
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
            newRole.setId(id);
            newRole.setName(roleName);
            log.info("Creating role: {}", roleName);
            return roleRepository.save(newRole);
        });
    }


    private void createHotelForAdminIfNotFound(String adminEmail) {
        userRepository.findByEmail(adminEmail).ifPresent(admin -> {
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
                hotel.setUser(admin);

                hotelRepository.save(hotel);
                log.info("Created default Hotel for Admin: {}", adminEmail);
            }
        });
    }
}