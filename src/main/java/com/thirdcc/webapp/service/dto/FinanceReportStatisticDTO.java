package com.thirdcc.webapp.service.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class FinanceReportStatisticDTO {
    
    private BigDecimal realiseIncome;
    private BigDecimal pendingIncome;
    private BigDecimal realiseExpense;
    private BigDecimal pendingExpense;
    private BigDecimal invalidExpense;
    private BigDecimal badDebt;

    public BigDecimal getRealiseIncome() {
        return realiseIncome;
    }

    public void setRealiseIncome(BigDecimal realiseIncome) {
        this.realiseIncome = realiseIncome;
    }

    public BigDecimal getPendingIncome() {
        return pendingIncome;
    }

    public void setPendingIncome(BigDecimal pendingIncome) {
        this.pendingIncome = pendingIncome;
    }

    public BigDecimal getRealiseExpense() {
        return realiseExpense;
    }

    public void setRealiseExpense(BigDecimal realisedExpense) {
        this.realiseExpense = realisedExpense;
    }

    public BigDecimal getPendingExpense() {
        return pendingExpense;
    }

    public void setPendingExpense(BigDecimal pendingExpense) {
        this.pendingExpense = pendingExpense;
    }

    public BigDecimal getInvalidExpense() {
        return invalidExpense;
    }

    public void setInvalidExpense(BigDecimal invalidExpense) {
        this.invalidExpense = invalidExpense;
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
        return Objects.equals(realiseIncome, that.realiseIncome) &&
            Objects.equals(pendingIncome, that.pendingIncome) &&
            Objects.equals(realiseExpense, that.realiseExpense) &&
            Objects.equals(pendingExpense, that.pendingExpense) &&
            Objects.equals(invalidExpense, that.invalidExpense) &&
            Objects.equals(badDebt, that.badDebt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(realiseIncome, pendingIncome, realiseExpense, pendingExpense, invalidExpense, badDebt);
    }
    
    @Override
    public String toString() {
        return "FinanceReportStatisticDTO{" +
            "realisedIncome=" + realiseIncome +
            ", pendingIncome=" + pendingIncome +
            ", realisedExpense=" + realiseExpense +
            ", pendingExpense=" + pendingExpense +
            ", invalidExpense=" + invalidExpense +
            ", badDebt=" + badDebt +
            "}";
    }
}
