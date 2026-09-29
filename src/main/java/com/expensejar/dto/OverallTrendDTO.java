package com.expensejar.dto;

import java.util.List;

public class OverallTrendDTO {
    private Long userId;
    private String userName;
    private int monthsRequested;
    private List<MonthTrendDTO> trends;

    public OverallTrendDTO() {}

    public OverallTrendDTO(Long userId, String userName, int monthsRequested, List<MonthTrendDTO> trends) {
        this.userId = userId;
        this.userName = userName;
        this.monthsRequested = monthsRequested;
        this.trends = trends;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getMonthsRequested() { return monthsRequested; }
    public void setMonthsRequested(int monthsRequested) { this.monthsRequested = monthsRequested; }

    public List<MonthTrendDTO> getTrends() { return trends; }
    public void setTrends(List<MonthTrendDTO> trends) { this.trends = trends; }

    public static OverallTrendDTOBuilder builder() { return new OverallTrendDTOBuilder(); }

    public static class OverallTrendDTOBuilder {
        private Long userId;
        private String userName;
        private int monthsRequested;
        private List<MonthTrendDTO> trends;

        public OverallTrendDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public OverallTrendDTOBuilder userName(String userName) { this.userName = userName; return this; }
        public OverallTrendDTOBuilder monthsRequested(int monthsRequested) { this.monthsRequested = monthsRequested; return this; }
        public OverallTrendDTOBuilder trends(List<MonthTrendDTO> trends) { this.trends = trends; return this; }

        public OverallTrendDTO build() {
            return new OverallTrendDTO(userId, userName, monthsRequested, trends);
        }
    }
}
