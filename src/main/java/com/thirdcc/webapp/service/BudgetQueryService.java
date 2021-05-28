package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.*; // for static metamodels
import com.thirdcc.webapp.domain.Budget;
import com.thirdcc.webapp.repository.BudgetRepository;
import com.thirdcc.webapp.service.criteria.BudgetCriteria;
import com.thirdcc.webapp.service.dto.BudgetDTO;
import com.thirdcc.webapp.service.mapper.BudgetMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Budget} entities in the database.
 * The main input is a {@link BudgetCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BudgetDTO} or a {@link Page} of {@link BudgetDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BudgetQueryService extends QueryService<Budget> {

  private final Logger log = LoggerFactory.getLogger(BudgetQueryService.class);

  private final BudgetRepository budgetRepository;

  private final BudgetMapper budgetMapper;

  public BudgetQueryService(BudgetRepository budgetRepository, BudgetMapper budgetMapper) {
    this.budgetRepository = budgetRepository;
    this.budgetMapper = budgetMapper;
  }

  /**
   * Return a {@link List} of {@link BudgetDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public List<BudgetDTO> findByCriteria(BudgetCriteria criteria) {
    log.debug("find by criteria : {}", criteria);
    final Specification<Budget> specification = createSpecification(criteria);
    return budgetMapper.toDto(budgetRepository.findAll(specification));
  }

  /**
   * Return a {@link Page} of {@link BudgetDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<BudgetDTO> findByCriteria(BudgetCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<Budget> specification = createSpecification(criteria);
    return budgetRepository.findAll(specification, page).map(budgetMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(BudgetCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<Budget> specification = createSpecification(criteria);
    return budgetRepository.count(specification);
  }

  /**
   * Function to convert {@link BudgetCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<Budget> createSpecification(BudgetCriteria criteria) {
    Specification<Budget> specification = Specification.where(null);
    if (criteria != null) {
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), Budget_.id));
      }
      if (criteria.getEventId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getEventId(), Budget_.eventId));
      }
      if (criteria.getAmount() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getAmount(), Budget_.amount));
      }
      if (criteria.getType() != null) {
        specification = specification.and(buildSpecification(criteria.getType(), Budget_.type));
      }
      if (criteria.getName() != null) {
        specification = specification.and(buildStringSpecification(criteria.getName(), Budget_.name));
      }
    }
    return specification;
  }
}
