package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.*; // for static metamodels
import com.thirdcc.webapp.domain.EventCrew;
import com.thirdcc.webapp.repository.EventCrewRepository;
import com.thirdcc.webapp.service.criteria.EventCrewCriteria;
import com.thirdcc.webapp.service.dto.EventCrewDTO;
import com.thirdcc.webapp.service.mapper.EventCrewMapper;
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
 * Service for executing complex queries for {@link EventCrew} entities in the database.
 * The main input is a {@link EventCrewCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EventCrewDTO} or a {@link Page} of {@link EventCrewDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EventCrewQueryService extends QueryService<EventCrew> {

  private final Logger log = LoggerFactory.getLogger(EventCrewQueryService.class);

  private final EventCrewRepository eventCrewRepository;

  private final EventCrewMapper eventCrewMapper;

  public EventCrewQueryService(EventCrewRepository eventCrewRepository, EventCrewMapper eventCrewMapper) {
    this.eventCrewRepository = eventCrewRepository;
    this.eventCrewMapper = eventCrewMapper;
  }

  /**
   * Return a {@link List} of {@link EventCrewDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public List<EventCrewDTO> findByCriteria(EventCrewCriteria criteria) {
    log.debug("find by criteria : {}", criteria);
    final Specification<EventCrew> specification = createSpecification(criteria);
    return eventCrewMapper.toDto(eventCrewRepository.findAll(specification));
  }

  /**
   * Return a {@link Page} of {@link EventCrewDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<EventCrewDTO> findByCriteria(EventCrewCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<EventCrew> specification = createSpecification(criteria);
    return eventCrewRepository.findAll(specification, page).map(eventCrewMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(EventCrewCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<EventCrew> specification = createSpecification(criteria);
    return eventCrewRepository.count(specification);
  }

  /**
   * Function to convert {@link EventCrewCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<EventCrew> createSpecification(EventCrewCriteria criteria) {
    Specification<EventCrew> specification = Specification.where(null);
    if (criteria != null) {
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), EventCrew_.id));
      }
      if (criteria.getUserId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getUserId(), EventCrew_.userId));
      }
      if (criteria.getEventId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getEventId(), EventCrew_.eventId));
      }
      if (criteria.getRole() != null) {
        specification = specification.and(buildSpecification(criteria.getRole(), EventCrew_.role));
      }
    }
    return specification;
  }
}
