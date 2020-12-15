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
import com.thirdcc.webapp.utils.YearSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FinanceReportServiceImpl implements FinanceReportService {

    private final Logger log = LoggerFactory.getLogger(FinanceReportServiceImpl.class);

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
    @Transactional(readOnly = true)
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

    @Override
    @Transactional(readOnly = true)
    public Optional<FinanceReportDTO> findOneByEventId(Long eventId) {
        return eventService
            .findOne(eventId)
            .map(this::toFinanceReportDTO)
            .map(this::mapTotalBudgetIncome)
            .map(this::mapTotalBudgetExpenses)
            .map(this::mapTotalIncome)
            .map(this::mapTotalExpenses);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<TransactionType, Map<Month, BigDecimal>> getFinanceReportByYearSession(String yearSession) {
        ZoneId zoneId = ZoneId.systemDefault();
        Instant inclusiveFrom = YearSessionUtils.getFirstInstantOfYearSession(yearSession);
        Instant exclusiveTo = inclusiveFrom.atZone(zoneId).toLocalDate().plusYears(1).atStartOfDay().atZone(zoneId).toInstant();
        log.info("getYearSessionFinanceReport inclusiveFrom {} exclusiveTo {}", inclusiveFrom, exclusiveTo);

        List<Transaction> transactionList = transactionRepository
            .findAllByCreatedDateGreaterThanEqualAndCreatedDateLessThan(inclusiveFrom, exclusiveTo);

        Map<TransactionType, Map<Month, BigDecimal>> result = new HashMap<>();
        result.put(TransactionType.INCOME, null);
        result.put(TransactionType.EXPENSE, null);

        for (Map.Entry<TransactionType, Map<Month, BigDecimal>> entry : result.entrySet()) {
            Map<Month, BigDecimal> defaultMap = new HashMap<>();
            YearSessionUtils.getAllMonthsOfYearSession()
                .forEach(month -> defaultMap.put(month, BigDecimal.ZERO));

            entry.setValue(defaultMap);
        }

        for (Transaction transaction : transactionList) {
            Month transactionMonth = transaction.getCreatedDate().atZone(zoneId).getMonth();
            TransactionType transactionType = transaction.getType();
            BigDecimal transactionAmount = transaction.getAmount();

            BigDecimal currentValue = result.get(transactionType).get(transactionMonth);
            result.get(transactionType).put(transactionMonth, currentValue.add(transactionAmount));
        }
        return result;
    }

    private FinanceReportDTO toFinanceReportDTO(EventDTO eventDTO) {
        FinanceReportDTO financeReportDTO = new FinanceReportDTO();
        financeReportDTO.setEventDTO(eventDTO);
        return financeReportDTO;
    }

    private FinanceReportDTO mapTotalExpenses(FinanceReportDTO financeReportDTO) {
        BigDecimal totalExpenses = transactionRepository
            .findAllByEventIdAndType(financeReportDTO.getEventDTO().getId(), TransactionType.EXPENSE)
            .stream()
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        financeReportDTO.setTotalExpenses(totalExpenses);
        return financeReportDTO;
    }

    private FinanceReportDTO mapTotalIncome(FinanceReportDTO financeReportDTO) {
        BigDecimal totalIncome = transactionRepository
            .findAllByEventIdAndType(financeReportDTO.getEventDTO().getId(), TransactionType.INCOME)
            .stream()
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        financeReportDTO.setTotalIncome(totalIncome);
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
        financeReportDTO.setTotalBudgetIncome(totalBudgetIncome);
        return financeReportDTO;
    }
}
