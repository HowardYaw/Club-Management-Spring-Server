package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.*; // for static metamodels
import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.service.criteria.EventCriteria;
import com.thirdcc.webapp.service.dto.EventDTO;
import com.thirdcc.webapp.service.mapper.EventMapper;
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
 * Service for executing complex queries for {@link Event} entities in the database.
 * The main input is a {@link EventCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EventDTO} or a {@link Page} of {@link EventDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EventQueryService extends QueryService<Event> {

  private final Logger log = LoggerFactory.getLogger(EventQueryService.class);

  private final EventRepository eventRepository;

  private final EventMapper eventMapper;

  public EventQueryService(EventRepository eventRepository, EventMapper eventMapper) {
    this.eventRepository = eventRepository;
    this.eventMapper = eventMapper;
  }

  /**
   * Return a {@link List} of {@link EventDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public List<EventDTO> findByCriteria(EventCriteria criteria) {
    log.debug("find by criteria : {}", criteria);
    final Specification<Event> specification = createSpecification(criteria);
    return eventMapper.toDto(eventRepository.findAll(specification));
  }

  /**
   * Return a {@link Page} of {@link EventDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<EventDTO> findByCriteria(EventCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<Event> specification = createSpecification(criteria);
    return eventRepository.findAll(specification, page).map(eventMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(EventCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<Event> specification = createSpecification(criteria);
    return eventRepository.count(specification);
  }

  /**
   * Function to convert {@link EventCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<Event> createSpecification(EventCriteria criteria) {
    Specification<Event> specification = Specification.where(null);
    if (criteria != null) {
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), Event_.id));
      }
      if (criteria.getName() != null) {
        specification = specification.and(buildStringSpecification(criteria.getName(), Event_.name));
      }
      if (criteria.getRemarks() != null) {
        specification = specification.and(buildStringSpecification(criteria.getRemarks(), Event_.remarks));
      }
      if (criteria.getVenue() != null) {
        specification = specification.and(buildStringSpecification(criteria.getVenue(), Event_.venue));
      }
      if (criteria.getStartDate() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getStartDate(), Event_.startDate));
      }
      if (criteria.getEndDate() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getEndDate(), Event_.endDate));
      }
      if (criteria.getFee() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getFee(), Event_.fee));
      }
      if (criteria.getRequiredTransport() != null) {
        specification = specification.and(buildSpecification(criteria.getRequiredTransport(), Event_.requiredTransport));
      }
      if (criteria.getStatus() != null) {
        specification = specification.and(buildSpecification(criteria.getStatus(), Event_.status));
      }
    }
    return specification;
  }
}
