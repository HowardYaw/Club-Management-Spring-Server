package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.domain.Budget;
import com.thirdcc.webapp.domain.Transaction;
import com.thirdcc.webapp.domain.enumeration.TransactionType;
import com.thirdcc.webapp.repository.BudgetRepository;
import com.thirdcc.webapp.repository.TransactionRepository;
import com.thirdcc.webapp.service.EventService;
import com.thirdcc.webapp.service.FinanceReportService;
import com.thirdcc.webapp.service.dto.EventDTO;
import com.thirdcc.webapp.service.dto.FinanceReportDTO;
import com.thirdcc.webapp.utils.PageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class FinanceReportServiceImpl implements FinanceReportService {

    private final EventService eventService;

    private final BudgetRepository budgetRepository;

    private final TransactionRepository transactionRepository;

    public FinanceReportServiceImpl(
        EventService eventService,
        BudgetRepository budgetRepository,
        TransactionRepository transactionRepository
    ) {
        this.eventService = eventService;
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Page<FinanceReportDTO> findAll(Pageable pageable) {
        List<FinanceReportDTO> financeReportDTOList = eventService
            .findAll(Pageable.unpaged())
            .stream()
            .map(this::toFinanceReportDTO)
            .map(this::mapTotalBudgetIncome)
            .map(this::mapTotalBudgetExpenses)
            .map(this::mapTotalIncome)
            .map(this::mapTotalExpenses)
            .collect(Collectors.toList());
        return PageUtils.toPage(financeReportDTOList, pageable);
    }

    private FinanceReportDTO toFinanceReportDTO(EventDTO eventDTO) {
        FinanceReportDTO financeReportDTO = new FinanceReportDTO();
        financeReportDTO.setEventDTO(eventDTO);
        return financeReportDTO;
    }

    private FinanceReportDTO mapTotalExpenses(FinanceReportDTO financeReportDTO) {
        BigDecimal totalBudgetExpenses = transactionRepository
            .findAllByEventIdAndType(financeReportDTO.getEventDTO().getId(), TransactionType.EXPENSE)
            .stream()
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        financeReportDTO.setTotalBudgetExpenses(totalBudgetExpenses);
        return financeReportDTO;
    }

    private FinanceReportDTO mapTotalIncome(FinanceReportDTO financeReportDTO) {
        BigDecimal totalBudgetIncome = transactionRepository
            .findAllByEventIdAndType(financeReportDTO.getEventDTO().getId(), TransactionType.INCOME)
            .stream()
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        financeReportDTO.setTotalBudgetExpenses(totalBudgetIncome);
        return financeReportDTO;
    }

    private FinanceReportDTO mapTotalBudgetExpenses(FinanceReportDTO financeReportDTO) {
        BigDecimal totalBudgetExpenses = budgetRepository
            .findAllByEventIdAndType(financeReportDTO.getEventDTO().getId(), TransactionType.EXPENSE)
            .stream()
            .map(Budget::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        financeReportDTO.setTotalBudgetExpenses(totalBudgetExpenses);
        return financeReportDTO;
    }

    private FinanceReportDTO mapTotalBudgetIncome(FinanceReportDTO financeReportDTO) {
        BigDecimal totalBudgetIncome = budgetRepository
            .findAllByEventIdAndType(financeReportDTO.getEventDTO().getId(), TransactionType.INCOME)
            .stream()
            .map(Budget::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        financeReportDTO.setTotalBudgetExpenses(totalBudgetIncome);
        return financeReportDTO;
    }
}
