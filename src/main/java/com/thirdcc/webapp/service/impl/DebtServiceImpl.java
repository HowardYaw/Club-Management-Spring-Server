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
        return debtRepository.findAllByStatusIn(pageable, openDebtStatus)
            .map(debtMapper::toDto);
    }
}
