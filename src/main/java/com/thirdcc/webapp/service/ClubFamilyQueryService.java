package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.*; // for static metamodels
import com.thirdcc.webapp.domain.ClubFamily;
import com.thirdcc.webapp.repository.ClubFamilyRepository;
import com.thirdcc.webapp.service.criteria.ClubFamilyCriteria;
import com.thirdcc.webapp.service.dto.ClubFamilyDTO;
import com.thirdcc.webapp.service.mapper.ClubFamilyMapper;
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
 * Service for executing complex queries for {@link ClubFamily} entities in the database.
 * The main input is a {@link ClubFamilyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ClubFamilyDTO} or a {@link Page} of {@link ClubFamilyDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ClubFamilyQueryService extends QueryService<ClubFamily> {

  private final Logger log = LoggerFactory.getLogger(ClubFamilyQueryService.class);

  private final ClubFamilyRepository clubFamilyRepository;

  private final ClubFamilyMapper clubFamilyMapper;

  public ClubFamilyQueryService(ClubFamilyRepository clubFamilyRepository, ClubFamilyMapper clubFamilyMapper) {
    this.clubFamilyRepository = clubFamilyRepository;
    this.clubFamilyMapper = clubFamilyMapper;
  }

  /**
   * Return a {@link List} of {@link ClubFamilyDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public List<ClubFamilyDTO> findByCriteria(ClubFamilyCriteria criteria) {
    log.debug("find by criteria : {}", criteria);
    final Specification<ClubFamily> specification = createSpecification(criteria);
    return clubFamilyMapper.toDto(clubFamilyRepository.findAll(specification));
  }

  /**
   * Return a {@link Page} of {@link ClubFamilyDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<ClubFamilyDTO> findByCriteria(ClubFamilyCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<ClubFamily> specification = createSpecification(criteria);
    return clubFamilyRepository.findAll(specification, page).map(clubFamilyMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(ClubFamilyCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<ClubFamily> specification = createSpecification(criteria);
    return clubFamilyRepository.count(specification);
  }

  /**
   * Function to convert {@link ClubFamilyCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<ClubFamily> createSpecification(ClubFamilyCriteria criteria) {
    Specification<ClubFamily> specification = Specification.where(null);
    if (criteria != null) {
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), ClubFamily_.id));
      }
      if (criteria.getName() != null) {
        specification = specification.and(buildStringSpecification(criteria.getName(), ClubFamily_.name));
      }
    }
    return specification;
  }
}
