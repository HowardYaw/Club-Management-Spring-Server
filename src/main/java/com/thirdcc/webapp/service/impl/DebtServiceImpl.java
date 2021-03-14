package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.service.DebtService;
import com.thirdcc.webapp.domain.Debt;
import com.thirdcc.webapp.domain.enumeration.DebtStatus;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.DebtRepository;
import com.thirdcc.webapp.repository.EventAttendeeRepository;
import com.thirdcc.webapp.service.dto.DebtDTO;
import com.thirdcc.webapp.service.mapper.DebtMapper;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Debt}.
 */
@Service
@Transactional
public class DebtServiceImpl implements DebtService {

    private final Logger log = LoggerFactory.getLogger(DebtServiceImpl.class);

    private final DebtRepository debtRepository;
    
    private final EventAttendeeRepository eventAttendeeRepository;

    private final DebtMapper debtMapper;

    public DebtServiceImpl(DebtRepository debtRepository, DebtMapper debtMapper, EventAttendeeRepository eventAttendeeRepository) {
        this.debtRepository = debtRepository;
        this.debtMapper = debtMapper;
        this.eventAttendeeRepository = eventAttendeeRepository;
    }

//    /**
//     * Save a debt.
//     *
//     * @param debtDTO the entity to save.
//     * @return the persisted entity.
//     */
//    @Override
//    public DebtDTO save(DebtDTO debtDTO) {
//        log.debug("Request to save Debt : {}", debtDTO);
//        Debt debt = debtMapper.toEntity(debtDTO);
//        debt = debtRepository.save(debt);
//        return debtMapper.toDto(debt);
//    }
    
//    /**
//     * Update a debt.
//     *
//     * @param debtDTO the entity to update.
//     * @return the persisted entity.
//     */
//    @Override
//    public DebtDTO update(DebtDTO debtDTO) {
//        log.debug("Request to save Debt : {}", debtDTO);
//        Debt debt = debtRepository.findById(debtDTO.getId())
//            .orElseThrow(() -> new BadRequestException("Event Checklist not found"));
//        debt.setAmount(debtDTO.getAmount());
//        debt.setEventAttendeeId(debtDTO.getEventAttendeeId());
//        debt.setReceiptId(debtDTO.getEventAttendeeId());
//        debt.setStatus(debtDTO.getStatus());
//        return debtMapper.toDto(
//            debtRepository.save(debt)
//        );
//    }

//    /**
//     * Get all the debts.
//     *
//     * @param pageable the pagination information.
//     * @return the list of entities.
//     */
//    @Override
//    @Transactional(readOnly = true)
//    public Page<DebtDTO> findAll(Pageable pageable) {
//        log.debug("Request to get all Debts");
//        return debtRepository.findAll(pageable)
//            .map(debtMapper::toDto);
//    }


//    /**
//     * Get one debt by id.
//     *
//     * @param id the id of the entity.
//     * @return the entity.
//     */
//    @Override
//    @Transactional(readOnly = true)
//    public Optional<DebtDTO> findOne(Long id) {
//        log.debug("Request to get Debt : {}", id);
//        return debtRepository.findById(id)
//            .map(debtMapper::toDto);
//    }

//    /**
//     * Delete the debt by id.
//     *
//     * @param id the id of the entity.
//     */
//    @Override
//    public void delete(Long id) {
//        log.debug("Request to delete Debt : {}", id);
//        debtRepository.deleteById(id);
//    }

    /**
     * Update the debtStatus of the debt to "debtStatus" for "id" debt
     *
     * @param id the id of the entity
     * @param debtStatus the new debtStatus of the entity
     * @return the entity.
     */
    @Override
    public DebtDTO updateStatus(Long id, DebtStatus debtStatus) {
        log.debug("Request to update status of debt: {}, to {}", id, debtStatus);
        Debt debt = debtRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("Debt not exists: " + id));
        if(!DebtStatus.OPEN.equals(debt.getStatus())){
            throw new BadRequestException("Debt is not open, not allow to update");
        }
        debt.setStatus(debtStatus);
        return debtMapper.toDto(
            debtRepository.save(debt)
        );
    }
    
    /**
     * Get all debts with OPEN status.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DebtDTO> findAllOpenDebts(Pageable pageable) {
        log.debug("Request to get all Debts");
        HashSet<DebtStatus> openDebtStatus = new HashSet<DebtStatus>() {{
            add(DebtStatus.OPEN);
        }};
        return debtRepository.findAllByStatusInOrderByEventAttendeeIdAscCreatedDateAsc(pageable, openDebtStatus)
            .map(debtMapper::toDto);
    }
}
