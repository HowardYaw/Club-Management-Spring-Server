package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.service.DebtService;
import com.thirdcc.webapp.domain.Debt;
import com.thirdcc.webapp.repository.DebtRepository;
import com.thirdcc.webapp.service.dto.DebtDTO;
import com.thirdcc.webapp.service.mapper.DebtMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Debt}.
 */
@Service
@Transactional
public class DebtServiceImpl implements DebtService {

    private final Logger log = LoggerFactory.getLogger(DebtServiceImpl.class);

    private final DebtRepository debtRepository;

    private final DebtMapper debtMapper;

    public DebtServiceImpl(DebtRepository debtRepository, DebtMapper debtMapper) {
        this.debtRepository = debtRepository;
        this.debtMapper = debtMapper;
    }

    /**
     * Save a debt.
     *
     * @param debtDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public DebtDTO save(DebtDTO debtDTO) {
        log.debug("Request to save Debt : {}", debtDTO);
        Debt debt = debtMapper.toEntity(debtDTO);
        debt = debtRepository.save(debt);
        return debtMapper.toDto(debt);
    }

    /**
     * Get all the debts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DebtDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Debts");
        return debtRepository.findAll(pageable)
            .map(debtMapper::toDto);
    }


    /**
     * Get one debt by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<DebtDTO> findOne(Long id) {
        log.debug("Request to get Debt : {}", id);
        return debtRepository.findById(id)
            .map(debtMapper::toDto);
    }

    /**
     * Delete the debt by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Debt : {}", id);
        debtRepository.deleteById(id);
    }
}
