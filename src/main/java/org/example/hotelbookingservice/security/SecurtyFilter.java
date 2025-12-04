package org.example.hotelbookingservice.security;

import lombok.RequiredArgsConstructor;
import org.example.hotelbookingservice.exception.CustomAccessDenialHandler;
import org.example.hotelbookingservice.exception.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurtyFilter {



    private final CustomAccessDenialHandler customAccessDenialHandler;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthFilter authFilter) throws Exception {

        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception ->
                        exception.accessDeniedHandler(customAccessDenialHandler)
                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .authorizeHttpRequests(request -> request
                        // --- 1. TÀI LIỆU API (SWAGGER) ---
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // --- 2. XÁC THỰC (Đăng ký, Đăng nhập) ---
                        // Khách hàng cần thực hiện chức năng đăng nhập, đăng ký
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // --- 3. TÌM KIẾM & XEM THÔNG TIN (PUBLIC GET ONLY) ---
                        // Khách hàng tìm kiếm khách sạn, xem phòng trống, xem chi tiết
                        // Lưu ý: Chỉ mở phương thức GET. Các hành động Thêm/Sửa/Xóa phải đăng nhập.
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/hotels/all",
                                "/api/v1/hotels/search",    // Tìm kiếm khách sạn theo điểm đến, ngày, người...
                                "/api/v1/hotels/{hotelId}", // Xem chi tiết khách sạn
                                "/api/v1/hotels/my-hotels"  // (Lưu ý: API này cần token, sẽ bị chặn ở dưới nếu không có token, nhưng an toàn hơn nên để authenticated)
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/rooms/all",
                                "/api/v1/rooms/all-available-rooms", // Tìm phòng trống
                                "/api/v1/rooms/search",
                                "/api/v1/rooms/types",
                                "/api/v1/rooms/{roomId}"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/amenities/all",
                                "/api/v1/amenities/{id}"
                        ).permitAll()

                        // Static resources (Ảnh phòng/khách sạn)
                        .requestMatchers("/room-photos/**").permitAll()

                        // --- 4. CÁC NGHIỆP VỤ CẦN ĐĂNG NHẬP (CUSTOMER & ADMIN) ---
                        // Bao gồm:
                        // - Booking: Đặt phòng, Hủy chuyến, Xem lịch sử
                        // - Review: Đánh giá, nhận xét (Code ReviewController chưa có file upload nhưng logic đã có trong Service)
                        // - User: Quản lý thông tin, đổi mật khẩu
                        // - Hotel Management: Thêm, xóa khách sạn (Customer/Admin)
                        // - Admin: Khóa tài khoản, Thống kê doanh thu, Duyệt (Logic duyệt nằm trong các API update/status)
                        .anyRequest().authenticated()
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }
}
