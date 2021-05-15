package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.*; // for static metamodels
import com.thirdcc.webapp.domain.EventImage;
import com.thirdcc.webapp.repository.EventImageRepository;
import com.thirdcc.webapp.service.criteria.EventImageCriteria;
import com.thirdcc.webapp.service.dto.EventImageDTO;
import com.thirdcc.webapp.service.mapper.EventImageMapper;
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
 * Service for executing complex queries for {@link EventImage} entities in the database.
 * The main input is a {@link EventImageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EventImageDTO} or a {@link Page} of {@link EventImageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EventImageQueryService extends QueryService<EventImage> {

  private final Logger log = LoggerFactory.getLogger(EventImageQueryService.class);

  private final EventImageRepository eventImageRepository;

  private final EventImageMapper eventImageMapper;

  public EventImageQueryService(EventImageRepository eventImageRepository, EventImageMapper eventImageMapper) {
    this.eventImageRepository = eventImageRepository;
    this.eventImageMapper = eventImageMapper;
  }

  /**
   * Return a {@link List} of {@link EventImageDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public List<EventImageDTO> findByCriteria(EventImageCriteria criteria) {
    log.debug("find by criteria : {}", criteria);
    final Specification<EventImage> specification = createSpecification(criteria);
    return eventImageMapper.toDto(eventImageRepository.findAll(specification));
  }

  /**
   * Return a {@link Page} of {@link EventImageDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<EventImageDTO> findByCriteria(EventImageCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<EventImage> specification = createSpecification(criteria);
    return eventImageRepository.findAll(specification, page).map(eventImageMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(EventImageCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<EventImage> specification = createSpecification(criteria);
    return eventImageRepository.count(specification);
  }

  /**
   * Function to convert {@link EventImageCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<EventImage> createSpecification(EventImageCriteria criteria) {
    Specification<EventImage> specification = Specification.where(null);
    if (criteria != null) {
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), EventImage_.id));
      }
      if (criteria.getEventId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getEventId(), EventImage_.eventId));
      }
      if (criteria.getImageStorageId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getImageStorageId(), EventImage_.imageStorageId));
      }
    }
    return specification;
  }
}
