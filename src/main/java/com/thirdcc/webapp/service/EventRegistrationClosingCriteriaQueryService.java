package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.*; // for static metamodels
import com.thirdcc.webapp.domain.EventRegistrationClosingCriteria;
import com.thirdcc.webapp.repository.EventRegistrationClosingCriteriaRepository;
import com.thirdcc.webapp.service.criteria.EventRegistrationClosingCriteriaCriteria;
import com.thirdcc.webapp.service.dto.EventRegistrationClosingCriteriaDTO;
import com.thirdcc.webapp.service.mapper.EventRegistrationClosingCriteriaMapper;
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
 * Service for executing complex queries for {@link EventRegistrationClosingCriteria} entities in the database.
 * The main input is a {@link EventRegistrationClosingCriteriaCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EventRegistrationClosingCriteriaDTO} or a {@link Page} of {@link EventRegistrationClosingCriteriaDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EventRegistrationClosingCriteriaQueryService extends QueryService<EventRegistrationClosingCriteria> {

  private final Logger log = LoggerFactory.getLogger(EventRegistrationClosingCriteriaQueryService.class);

  private final EventRegistrationClosingCriteriaRepository eventRegistrationClosingCriteriaRepository;

  private final EventRegistrationClosingCriteriaMapper eventRegistrationClosingCriteriaMapper;

  public EventRegistrationClosingCriteriaQueryService(
    EventRegistrationClosingCriteriaRepository eventRegistrationClosingCriteriaRepository,
    EventRegistrationClosingCriteriaMapper eventRegistrationClosingCriteriaMapper
  ) {
    this.eventRegistrationClosingCriteriaRepository = eventRegistrationClosingCriteriaRepository;
    this.eventRegistrationClosingCriteriaMapper = eventRegistrationClosingCriteriaMapper;
  }

  /**
   * Return a {@link List} of {@link EventRegistrationClosingCriteriaDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public List<EventRegistrationClosingCriteriaDTO> findByCriteria(EventRegistrationClosingCriteriaCriteria criteria) {
    log.debug("find by criteria : {}", criteria);
    final Specification<EventRegistrationClosingCriteria> specification = createSpecification(criteria);
    return eventRegistrationClosingCriteriaMapper.toDto(eventRegistrationClosingCriteriaRepository.findAll(specification));
  }

  /**
   * Return a {@link Page} of {@link EventRegistrationClosingCriteriaDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<EventRegistrationClosingCriteriaDTO> findByCriteria(EventRegistrationClosingCriteriaCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<EventRegistrationClosingCriteria> specification = createSpecification(criteria);
    return eventRegistrationClosingCriteriaRepository.findAll(specification, page).map(eventRegistrationClosingCriteriaMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(EventRegistrationClosingCriteriaCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<EventRegistrationClosingCriteria> specification = createSpecification(criteria);
    return eventRegistrationClosingCriteriaRepository.count(specification);
  }

  /**
   * Function to convert {@link EventRegistrationClosingCriteriaCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<EventRegistrationClosingCriteria> createSpecification(EventRegistrationClosingCriteriaCriteria criteria) {
    Specification<EventRegistrationClosingCriteria> specification = Specification.where(null);
    if (criteria != null) {
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), EventRegistrationClosingCriteria_.id));
      }
      if (criteria.getEventId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getEventId(), EventRegistrationClosingCriteria_.eventId));
      }
      if (criteria.getMaxAttendees() != null) {
        specification =
          specification.and(buildRangeSpecification(criteria.getMaxAttendees(), EventRegistrationClosingCriteria_.maxAttendees));
      }
      if (criteria.getClosingDate() != null) {
        specification =
          specification.and(buildRangeSpecification(criteria.getClosingDate(), EventRegistrationClosingCriteria_.closingDate));
      }
      if (criteria.getForceClose() != null) {
        specification = specification.and(buildSpecification(criteria.getForceClose(), EventRegistrationClosingCriteria_.forceClose));
      }
    }
    return specification;
  }
}
