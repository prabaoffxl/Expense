package com.expensejar.service;

import com.expensejar.dto.DashboardDTO;

public interface DashboardService {
    /**
     * Get aggregate stats for the dashboard.
     */
    DashboardDTO getDashboardStats();
}
