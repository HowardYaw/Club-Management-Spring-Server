package com.thirdcc.webapp.service.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class FinanceReportDTO {

    private EventDTO eventDTO;
    private BigDecimal totalExpenses;
    private BigDecimal totalIncome;
    private BigDecimal totalBudgetExpenses;
    private BigDecimal totalBudgetIncome;

    public EventDTO getEventDTO() {
        return eventDTO;
    }

    public void setEventDTO(EventDTO eventDTO) {
        this.eventDTO = eventDTO;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalBudgetExpenses() {
        return totalBudgetExpenses;
    }

    public void setTotalBudgetExpenses(BigDecimal totalBudgetExpenses) {
        this.totalBudgetExpenses = totalBudgetExpenses;
    }

    public BigDecimal getTotalBudgetIncome() {
        return totalBudgetIncome;
    }

    public void setTotalBudgetIncome(BigDecimal totalBudgetIncome) {
        this.totalBudgetIncome = totalBudgetIncome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinanceReportDTO that = (FinanceReportDTO) o;
        return Objects.equals(eventDTO, that.eventDTO) &&
            Objects.equals(totalExpenses, that.totalExpenses) &&
            Objects.equals(totalIncome, that.totalIncome) &&
            Objects.equals(totalBudgetExpenses, that.totalBudgetExpenses) &&
            Objects.equals(totalBudgetIncome, that.totalBudgetIncome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventDTO, totalExpenses, totalIncome, totalBudgetExpenses, totalBudgetIncome);
    }
}
