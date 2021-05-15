package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.*; // for static metamodels
import com.thirdcc.webapp.domain.YearSession;
import com.thirdcc.webapp.repository.YearSessionRepository;
import com.thirdcc.webapp.service.criteria.YearSessionCriteria;
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
 * Service for executing complex queries for {@link YearSession} entities in the database.
 * The main input is a {@link YearSessionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link YearSession} or a {@link Page} of {@link YearSession} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class YearSessionQueryService extends QueryService<YearSession> {

  private final Logger log = LoggerFactory.getLogger(YearSessionQueryService.class);

  private final YearSessionRepository yearSessionRepository;

  public YearSessionQueryService(YearSessionRepository yearSessionRepository) {
    this.yearSessionRepository = yearSessionRepository;
  }

  /**
   * Return a {@link List} of {@link YearSession} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public List<YearSession> findByCriteria(YearSessionCriteria criteria) {
    log.debug("find by criteria : {}", criteria);
    final Specification<YearSession> specification = createSpecification(criteria);
    return yearSessionRepository.findAll(specification);
  }

  /**
   * Return a {@link Page} of {@link YearSession} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<YearSession> findByCriteria(YearSessionCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<YearSession> specification = createSpecification(criteria);
    return yearSessionRepository.findAll(specification, page);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(YearSessionCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<YearSession> specification = createSpecification(criteria);
    return yearSessionRepository.count(specification);
  }

  /**
   * Function to convert {@link YearSessionCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<YearSession> createSpecification(YearSessionCriteria criteria) {
    Specification<YearSession> specification = Specification.where(null);
    if (criteria != null) {
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), YearSession_.id));
      }
      if (criteria.getValue() != null) {
        specification = specification.and(buildStringSpecification(criteria.getValue(), YearSession_.value));
      }
    }
    return specification;
  }
}
