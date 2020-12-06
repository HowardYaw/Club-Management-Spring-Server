package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.domain.enumeration.ClaimStatus;
import com.thirdcc.webapp.domain.enumeration.TransactionStatus;
import com.thirdcc.webapp.domain.enumeration.TransactionType;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.service.ClaimService;
import com.thirdcc.webapp.service.EventService;
import com.thirdcc.webapp.service.ReceiptService;
import com.thirdcc.webapp.service.TransactionService;
import com.thirdcc.webapp.domain.Transaction;
import com.thirdcc.webapp.repository.TransactionRepository;
import com.thirdcc.webapp.service.dto.ClaimDTO;
import com.thirdcc.webapp.service.dto.ReceiptDTO;
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

    private final ReceiptService receiptService;

    private final ClaimService claimService;

    public TransactionServiceImpl(
        TransactionRepository transactionRepository,
        TransactionMapper transactionMapper,
        EventRepository eventRepository,
        EventService eventService,
        ReceiptService receiptService,
        ClaimService claimService
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.receiptService = receiptService;
        this.claimService = claimService;
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
        eventService.findEventByIdAndNotCancelledStatus(transactionDTO.getEventId());
        if (transactionDTO.getType() == TransactionType.EXPENSE && transactionDTO.getReceiptDTO() == null) {
            throw new BadRequestException("Expense transaction required an receipt image as proof ");
        }
        if (transactionDTO.getReceiptDTO() != null) {
            ReceiptDTO receiptDTO = saveReceipt(transactionDTO.getReceiptDTO());
            transactionDTO.setReceiptId(receiptDTO.getId());
        }
        Transaction transaction = transactionMapper.toEntity(transactionDTO);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction = transactionRepository.save(transaction);
        if (transactionDTO.getType() == TransactionType.EXPENSE) {
            createClaimRecord(transaction);
        }
        return transactionMapper.toDto(transaction);
    }

    @Override
    public TransactionDTO update(TransactionDTO transactionDTO) {
        log.debug("Request to update Transaction: {}", transactionDTO);
        Transaction transaction = transactionRepository
            .findById(transactionDTO.getId())
            .orElseThrow(() -> new BadRequestException("Id not found when updating transaction"));
        if (transaction.getStatus() == TransactionStatus.CANCELLED) {
            throw new BadRequestException("Cannot update transaction that is cancelled");
        }
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setDetails(transactionDTO.getDetails());
        transaction.setStatus(transactionDTO.getStatus());
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
        log.debug("Request to find all Transaction of event: {}", eventId);
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

    private ReceiptDTO saveReceipt(ReceiptDTO receiptDTO) {
        receiptDTO = uploadReceipt(receiptDTO);
        return receiptService.save(receiptDTO);
    }

    private ReceiptDTO uploadReceipt(ReceiptDTO receiptDTO) {
        // TODO: upload receipt image to cloud storage
        // TODO: set info of uploaded receipt to receiptDTO
        return receiptDTO;
    }

    private ClaimDTO createClaimRecord(Transaction transaction) {
        ClaimDTO claimDTO = new ClaimDTO();
        claimDTO.setAmount(transaction.getAmount());
        claimDTO.setStatus(ClaimStatus.OPEN);
        claimDTO.setCreatedBy(transaction.getCreatedBy());
        claimDTO.setCreatedDate(transaction.getCreatedDate());
        claimDTO.setTransactionId(transaction.getId());
        return claimService.save(claimDTO);
    }
}
