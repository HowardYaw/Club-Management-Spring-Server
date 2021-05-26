package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.*; // for static metamodels
import com.thirdcc.webapp.domain.Receipt;
import com.thirdcc.webapp.repository.ReceiptRepository;
import com.thirdcc.webapp.service.criteria.ReceiptCriteria;
import com.thirdcc.webapp.service.dto.ReceiptDTO;
import com.thirdcc.webapp.service.mapper.ReceiptMapper;

import java.util.List;
import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Receipt} entities in the database.
 * The main input is a {@link ReceiptCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ReceiptDTO} or a {@link Page} of {@link ReceiptDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReceiptQueryService extends QueryService<Receipt> {

    private final Logger log = LoggerFactory.getLogger(ReceiptQueryService.class);

    private final ReceiptRepository receiptRepository;

    private final ReceiptMapper receiptMapper;

    public ReceiptQueryService(ReceiptRepository receiptRepository, ReceiptMapper receiptMapper) {
        this.receiptRepository = receiptRepository;
        this.receiptMapper = receiptMapper;
    }

    /**
     * Return a {@link List} of {@link ReceiptDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ReceiptDTO> findByCriteria(ReceiptCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Receipt> specification = createSpecification(criteria);
        return receiptMapper.toDto(receiptRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ReceiptDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ReceiptDTO> findByCriteria(ReceiptCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Receipt> specification = createSpecification(criteria);
        return receiptRepository.findAll(specification, page).map(receiptMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReceiptCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Receipt> specification = createSpecification(criteria);
        return receiptRepository.count(specification);
    }

    /**
     * Function to convert {@link ReceiptCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Receipt> createSpecification(ReceiptCriteria criteria) {
        Specification<Receipt> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Receipt_.id));
            }
            if (criteria.getReceiptUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReceiptUrl(), Receipt_.receiptUrl));
            }
            if (criteria.getFileName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFileName(), Receipt_.fileName));
            }
            if (criteria.getFileType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFileType(), Receipt_.fileType));
            }
            if (criteria.getReceiptUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReceiptUrl(), Receipt_.receiptUniqueId));
            }
        }
        return specification;
    }
}
