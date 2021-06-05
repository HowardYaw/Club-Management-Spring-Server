package com.thirdcc.webapp.service.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class FinanceReportStatisticDTO {
    
    private BigDecimal realisedIncome;
    private BigDecimal pendingIncome;
    private BigDecimal realisedExpenses;
    private BigDecimal pendingExpenses;
    private BigDecimal invalidExpenses;
    private BigDecimal badDebt;

    public BigDecimal getRealisedIncome() {
        return realisedIncome;
    }

    public void setRealisedIncome(BigDecimal realisedIncome) {
        this.realisedIncome = realisedIncome;
    }

    public BigDecimal getPendingIncome() {
        return pendingIncome;
    }

    public void setPendingIncome(BigDecimal pendingIncome) {
        this.pendingIncome = pendingIncome;
    }

    public BigDecimal getRealisedExpenses() {
        return realisedExpenses;
    }

    public void setRealisedExpenses(BigDecimal realisedExpenses) {
        this.realisedExpenses = realisedExpenses;
    }

    public BigDecimal getPendingExpenses() {
        return pendingExpenses;
    }

    public void setPendingExpenses(BigDecimal pendingExpenses) {
        this.pendingExpenses = pendingExpenses;
    }

    public BigDecimal getInvalidExpenses() {
        return invalidExpenses;
    }

    public void setInvalidExpenses(BigDecimal invalidExpenses) {
        this.invalidExpenses = invalidExpenses;
    }

    public BigDecimal getBadDebt() {
        return badDebt;
    }

    public void setBadDebt(BigDecimal badDebt) {
        this.badDebt = badDebt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinanceReportStatisticDTO that = (FinanceReportStatisticDTO) o;
        return Objects.equals(realisedIncome, that.realisedIncome) &&
            Objects.equals(pendingIncome, that.pendingIncome) &&
            Objects.equals(realisedExpenses, that.realisedExpenses) &&
            Objects.equals(pendingExpenses, that.pendingExpenses) &&
            Objects.equals(invalidExpenses, that.invalidExpenses) &&
            Objects.equals(badDebt, that.badDebt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(realisedIncome, pendingIncome, realisedExpenses, pendingExpenses, invalidExpenses, badDebt);
    }
}
