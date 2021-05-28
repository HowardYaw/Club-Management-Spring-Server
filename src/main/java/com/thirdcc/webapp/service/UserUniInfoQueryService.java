package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.*; // for static metamodels
import com.thirdcc.webapp.domain.UserUniInfo;
import com.thirdcc.webapp.repository.UserUniInfoRepository;
import com.thirdcc.webapp.service.criteria.UserUniInfoCriteria;
import com.thirdcc.webapp.service.dto.UserUniInfoDTO;
import com.thirdcc.webapp.service.mapper.UserUniInfoMapper;
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
 * Service for executing complex queries for {@link UserUniInfo} entities in the database.
 * The main input is a {@link UserUniInfoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserUniInfoDTO} or a {@link Page} of {@link UserUniInfoDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserUniInfoQueryService extends QueryService<UserUniInfo> {

  private final Logger log = LoggerFactory.getLogger(UserUniInfoQueryService.class);

  private final UserUniInfoRepository userUniInfoRepository;

  private final UserUniInfoMapper userUniInfoMapper;

  public UserUniInfoQueryService(UserUniInfoRepository userUniInfoRepository, UserUniInfoMapper userUniInfoMapper) {
    this.userUniInfoRepository = userUniInfoRepository;
    this.userUniInfoMapper = userUniInfoMapper;
  }

  /**
   * Return a {@link List} of {@link UserUniInfoDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public List<UserUniInfoDTO> findByCriteria(UserUniInfoCriteria criteria) {
    log.debug("find by criteria : {}", criteria);
    final Specification<UserUniInfo> specification = createSpecification(criteria);
    return userUniInfoMapper.toDto(userUniInfoRepository.findAll(specification));
  }

  /**
   * Return a {@link Page} of {@link UserUniInfoDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<UserUniInfoDTO> findByCriteria(UserUniInfoCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<UserUniInfo> specification = createSpecification(criteria);
    return userUniInfoRepository.findAll(specification, page).map(userUniInfoMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(UserUniInfoCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<UserUniInfo> specification = createSpecification(criteria);
    return userUniInfoRepository.count(specification);
  }

  /**
   * Function to convert {@link UserUniInfoCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<UserUniInfo> createSpecification(UserUniInfoCriteria criteria) {
    Specification<UserUniInfo> specification = Specification.where(null);
    if (criteria != null) {
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), UserUniInfo_.id));
      }
      if (criteria.getUserId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getUserId(), UserUniInfo_.userId));
      }
      if (criteria.getCourseProgramId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getCourseProgramId(), UserUniInfo_.courseProgramId));
      }
      if (criteria.getYearSession() != null) {
        specification = specification.and(buildStringSpecification(criteria.getYearSession(), UserUniInfo_.yearSession));
      }
      if (criteria.getIntakeSemester() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getIntakeSemester(), UserUniInfo_.intakeSemester));
      }
      if (criteria.getStayIn() != null) {
        specification = specification.and(buildStringSpecification(criteria.getStayIn(), UserUniInfo_.stayIn));
      }
      if (criteria.getStatus() != null) {
        specification = specification.and(buildSpecification(criteria.getStatus(), UserUniInfo_.status));
      }
    }
    return specification;
  }
}
