package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.*; // for static metamodels
import com.thirdcc.webapp.domain.EventAttendee;
import com.thirdcc.webapp.repository.EventAttendeeRepository;
import com.thirdcc.webapp.service.criteria.EventAttendeeCriteria;
import com.thirdcc.webapp.service.dto.EventAttendeeDTO;
import com.thirdcc.webapp.service.mapper.EventAttendeeMapper;
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
 * Service for executing complex queries for {@link EventAttendee} entities in the database.
 * The main input is a {@link EventAttendeeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EventAttendeeDTO} or a {@link Page} of {@link EventAttendeeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EventAttendeeQueryService extends QueryService<EventAttendee> {

  private final Logger log = LoggerFactory.getLogger(EventAttendeeQueryService.class);

  private final EventAttendeeRepository eventAttendeeRepository;

  private final EventAttendeeMapper eventAttendeeMapper;

  public EventAttendeeQueryService(EventAttendeeRepository eventAttendeeRepository, EventAttendeeMapper eventAttendeeMapper) {
    this.eventAttendeeRepository = eventAttendeeRepository;
    this.eventAttendeeMapper = eventAttendeeMapper;
  }

  /**
   * Return a {@link List} of {@link EventAttendeeDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public List<EventAttendeeDTO> findByCriteria(EventAttendeeCriteria criteria) {
    log.debug("find by criteria : {}", criteria);
    final Specification<EventAttendee> specification = createSpecification(criteria);
    return eventAttendeeMapper.toDto(eventAttendeeRepository.findAll(specification));
  }

  /**
   * Return a {@link Page} of {@link EventAttendeeDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<EventAttendeeDTO> findByCriteria(EventAttendeeCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<EventAttendee> specification = createSpecification(criteria);
    return eventAttendeeRepository.findAll(specification, page).map(eventAttendeeMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(EventAttendeeCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<EventAttendee> specification = createSpecification(criteria);
    return eventAttendeeRepository.count(specification);
  }

  /**
   * Function to convert {@link EventAttendeeCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<EventAttendee> createSpecification(EventAttendeeCriteria criteria) {
    Specification<EventAttendee> specification = Specification.where(null);
    if (criteria != null) {
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), EventAttendee_.id));
      }
      if (criteria.getUserId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getUserId(), EventAttendee_.userId));
      }
      if (criteria.getEventId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getEventId(), EventAttendee_.eventId));
      }
      if (criteria.getProvideTransport() != null) {
        specification = specification.and(buildSpecification(criteria.getProvideTransport(), EventAttendee_.provideTransport));
      }
    }
    return specification;
  }
}
