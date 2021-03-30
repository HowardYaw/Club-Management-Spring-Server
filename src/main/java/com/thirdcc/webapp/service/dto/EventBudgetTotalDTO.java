package com.thirdcc.webapp.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class EventBudgetTotalDTO implements Serializable {
    private BigDecimal totalExpense;
    private BigDecimal totalIncome;

    public EventBudgetTotalDTO() {
        totalExpense = BigDecimal.ZERO;
        totalIncome = BigDecimal.ZERO;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public void addTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = this.totalExpense.add(totalExpense);
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public void addTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = this.totalIncome.add(totalIncome);
    }
}
