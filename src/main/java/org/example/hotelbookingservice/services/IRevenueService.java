package org.example.hotelbookingservice.services;

import org.example.hotelbookingservice.dto.response.RevenueStatisticResponse;

import java.util.List;

public interface IRevenueService {
    List<RevenueStatisticResponse> getYearlyRevenueStatistics(int year);
}
