package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.service.EventService;
import com.thirdcc.webapp.service.TransactionService;
import com.thirdcc.webapp.domain.Transaction;
import com.thirdcc.webapp.repository.TransactionRepository;
import com.thirdcc.webapp.service.dto.TransactionDTO;
import com.thirdcc.webapp.service.mapper.TransactionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Transaction}.
 */
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final EventRepository eventRepository;

    private final EventService eventService;

    public TransactionServiceImpl(
        TransactionRepository transactionRepository,
        TransactionMapper transactionMapper,
        EventRepository eventRepository,
        EventService eventService
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    /**
     * Save a transaction.
     *
     * @param transactionDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public TransactionDTO save(TransactionDTO transactionDTO) {
        log.debug("Request to save Transaction : {}", transactionDTO);
        Transaction transaction = transactionMapper.toEntity(transactionDTO);
        transaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(transaction);
    }

    /**
     * Get all the transactions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Transactions");
        return transactionRepository.findAll(pageable)
            .map(transactionMapper::toDto)
            .map(this::mapEventName);
    }


    /**
     * Get one transaction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionDTO> findOne(Long id) {
        log.debug("Request to get Transaction : {}", id);
        return transactionRepository.findById(id)
            .map(transactionMapper::toDto);
    }

    /**
     * Delete the transaction by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Transaction : {}", id);
        transactionRepository.deleteById(id);
    }

    @Override
    public Page<TransactionDTO> findAllByEventId(Long eventId, Pageable pageable) {
        log.debug("Request to find all Transaction of event: {]", eventId);
        eventService.findEventByIdAndNotCancelledStatus(eventId);
        return transactionRepository.findAllByEventId(eventId, pageable)
            .map(transactionMapper::toDto);
    }

    private TransactionDTO mapEventName(TransactionDTO transactionDTO) {
        Event event = eventRepository.findById(transactionDTO.getEventId())
            .orElseThrow(() -> new BadRequestException("Event Not Found "+ transactionDTO.getEventId()));
        transactionDTO.setEventName(event.getName());
        return transactionDTO;
    }
}
