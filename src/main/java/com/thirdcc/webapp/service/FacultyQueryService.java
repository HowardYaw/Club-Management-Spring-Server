package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.*; // for static metamodels
import com.thirdcc.webapp.domain.Faculty;
import com.thirdcc.webapp.repository.FacultyRepository;
import com.thirdcc.webapp.service.criteria.FacultyCriteria;
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
 * Service for executing complex queries for {@link Faculty} entities in the database.
 * The main input is a {@link FacultyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Faculty} or a {@link Page} of {@link Faculty} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FacultyQueryService extends QueryService<Faculty> {

  private final Logger log = LoggerFactory.getLogger(FacultyQueryService.class);

  private final FacultyRepository facultyRepository;

  public FacultyQueryService(FacultyRepository facultyRepository) {
    this.facultyRepository = facultyRepository;
  }

  /**
   * Return a {@link List} of {@link Faculty} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public List<Faculty> findByCriteria(FacultyCriteria criteria) {
    log.debug("find by criteria : {}", criteria);
    final Specification<Faculty> specification = createSpecification(criteria);
    return facultyRepository.findAll(specification);
  }

  /**
   * Return a {@link Page} of {@link Faculty} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<Faculty> findByCriteria(FacultyCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<Faculty> specification = createSpecification(criteria);
    return facultyRepository.findAll(specification, page);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(FacultyCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<Faculty> specification = createSpecification(criteria);
    return facultyRepository.count(specification);
  }

  /**
   * Function to convert {@link FacultyCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<Faculty> createSpecification(FacultyCriteria criteria) {
    Specification<Faculty> specification = Specification.where(null);
    if (criteria != null) {
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), Faculty_.id));
      }
      if (criteria.getName() != null) {
        specification = specification.and(buildStringSpecification(criteria.getName(), Faculty_.name));
      }
      if (criteria.getShortName() != null) {
        specification = specification.and(buildStringSpecification(criteria.getShortName(), Faculty_.shortName));
      }
    }
    return specification;
  }
}
