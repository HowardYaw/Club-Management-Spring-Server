package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.*; // for static metamodels
import com.thirdcc.webapp.domain.EventActivity;
import com.thirdcc.webapp.repository.EventActivityRepository;
import com.thirdcc.webapp.service.criteria.EventActivityCriteria;
import com.thirdcc.webapp.service.dto.EventActivityDTO;
import com.thirdcc.webapp.service.mapper.EventActivityMapper;
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
 * Service for executing complex queries for {@link EventActivity} entities in the database.
 * The main input is a {@link EventActivityCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EventActivityDTO} or a {@link Page} of {@link EventActivityDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EventActivityQueryService extends QueryService<EventActivity> {

  private final Logger log = LoggerFactory.getLogger(EventActivityQueryService.class);

  private final EventActivityRepository eventActivityRepository;

  private final EventActivityMapper eventActivityMapper;

  public EventActivityQueryService(EventActivityRepository eventActivityRepository, EventActivityMapper eventActivityMapper) {
    this.eventActivityRepository = eventActivityRepository;
    this.eventActivityMapper = eventActivityMapper;
  }

  /**
   * Return a {@link List} of {@link EventActivityDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public List<EventActivityDTO> findByCriteria(EventActivityCriteria criteria) {
    log.debug("find by criteria : {}", criteria);
    final Specification<EventActivity> specification = createSpecification(criteria);
    return eventActivityMapper.toDto(eventActivityRepository.findAll(specification));
  }

  /**
   * Return a {@link Page} of {@link EventActivityDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<EventActivityDTO> findByCriteria(EventActivityCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<EventActivity> specification = createSpecification(criteria);
    return eventActivityRepository.findAll(specification, page).map(eventActivityMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(EventActivityCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<EventActivity> specification = createSpecification(criteria);
    return eventActivityRepository.count(specification);
  }

  /**
   * Function to convert {@link EventActivityCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<EventActivity> createSpecification(EventActivityCriteria criteria) {
    Specification<EventActivity> specification = Specification.where(null);
    if (criteria != null) {
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), EventActivity_.id));
      }
      if (criteria.getEventId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getEventId(), EventActivity_.eventId));
      }
      if (criteria.getStartDate() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getStartDate(), EventActivity_.startDate));
      }
      if (criteria.getDurationInDay() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getDurationInDay(), EventActivity_.durationInDay));
      }
      if (criteria.getName() != null) {
        specification = specification.and(buildStringSpecification(criteria.getName(), EventActivity_.name));
      }
    }
    return specification;
  }
}
