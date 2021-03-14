package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.service.BudgetService;
import com.thirdcc.webapp.domain.Budget;
import com.thirdcc.webapp.repository.BudgetRepository;
import com.thirdcc.webapp.service.EventService;
import com.thirdcc.webapp.service.dto.BudgetDTO;
import com.thirdcc.webapp.service.mapper.BudgetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Budget}.
 */
@Service
@Transactional
public class BudgetServiceImpl implements BudgetService {

    private final Logger log = LoggerFactory.getLogger(BudgetServiceImpl.class);

    private final BudgetRepository budgetRepository;

    private final BudgetMapper budgetMapper;

    private final EventService eventService;

    public BudgetServiceImpl(
        BudgetRepository budgetRepository,
        BudgetMapper budgetMapper,
        EventService eventService
    ) {
        this.budgetRepository = budgetRepository;
        this.budgetMapper = budgetMapper;
        this.eventService = eventService;
    }

    @Override
    public BudgetDTO save(BudgetDTO budgetDTO) {
        log.debug("Request to save Budget : {}", budgetDTO);
        Event event = eventService.findEventByIdAndNotCancelledStatus(budgetDTO.getEventId());
        if (event.getEndDate().isBefore(Instant.now())) {
            throw new BadRequestException("cannot save budget for ended event");
        }
        Budget budget = budgetMapper.toEntity(budgetDTO);
        budget = budgetRepository.save(budget);
        return budgetMapper.toDto(budget);
    }

    @Override
    public BudgetDTO update(BudgetDTO budgetDTO) {
        log.debug("Request to update Budget : {}", budgetDTO);
        Budget budget = budgetRepository
            .findById(budgetDTO.getId())
            .orElseThrow(() -> new BadRequestException("Cannot update non existing budget"));
        Event event = eventService.findEventByIdAndNotCancelledStatus(budget.getEventId());
        if (event.getEndDate().isBefore(Instant.now())) {
            throw new BadRequestException("cannot update budget for ended event");
        }
        budget.setAmount(budgetDTO.getAmount());
        budget.setType(budgetDTO.getType());
        budget.setName(budgetDTO.getName());
        budget.setDetails(budgetDTO.getDetails());
        budget = budgetRepository.save(budget);
        return budgetMapper.toDto(budget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetDTO> findAllByEventId(Pageable pageable, Long eventId) {
        log.debug("Request to findAllByEventId : {}", eventId);
        return budgetRepository.findAllByEventId(pageable, eventId)
            .stream()
            .map(budgetMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BudgetDTO> findOneByEventIdAndId(Long eventId, Long id) {
        log.debug("Request to get Budget eventId: {}, id: {}", eventId, id);
        return budgetRepository.findOneByEventIdAndId(eventId, id)
            .map(budgetMapper::toDto);
    }

    @Override
    public void delete(Long eventId, Long id) {
        log.debug("Request to delete Budget eventId: {}, id: {}", eventId, id);
        Budget budget = budgetRepository
            .findOneByEventIdAndId(eventId, id)
            .orElseThrow(() -> new BadRequestException("Cannot delete non existing budget"));
        budgetRepository.deleteById(id);
    }
}
