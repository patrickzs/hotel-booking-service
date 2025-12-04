package org.example.hotelbookingservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.hotelbookingservice.dto.response.RevenueStatisticResponse;
import org.example.hotelbookingservice.repository.BookingRepository;
import org.example.hotelbookingservice.services.IRevenueService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueServiceImpl implements IRevenueService {

    private final BookingRepository bookingRepository;
    //Suppose Admin takes 10% commission from total revenue
    private static final BigDecimal COMMISSION_RATE = BigDecimal.valueOf(0.10);

    @Override
    public List<RevenueStatisticResponse> getYearlyRevenueStatistics(int year) {
        // Call Repository to get raw data
        List<Object[]> results = bookingRepository.getRevenueStatistics(year);

        List<RevenueStatisticResponse> responses = new ArrayList<>();

        for (Object[] row : results) {
            // Map each column from sql query into variable
            Integer hotelId = (Integer) row[0];
            String hotelName = (String) row[1];
            Integer month = (Integer) row[2];
            Long totalBookings = (Long) row[3];

            // Handle TotalPrice (in DB is Float, need to convert to BigDecimal)
            Double totalPriceDouble = ((Number) row[4]).doubleValue();
            BigDecimal totalRevenue = BigDecimal.valueOf(totalPriceDouble);

            // Calculate commission
            BigDecimal adminCommission = totalRevenue.multiply(COMMISSION_RATE);

            // Build Response
            RevenueStatisticResponse response = RevenueStatisticResponse.builder()
                    .hotelId(hotelId)
                    .hotelName(hotelName)
                    .month(month)
                    .year(year)
                    .totalBookings(totalBookings.intValue())
                    .totalRevenue(totalRevenue)
                    .adminCommission(adminCommission)
                    .build();

            responses.add(response);
        }

        return responses;
    }
}
