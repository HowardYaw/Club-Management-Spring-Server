package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.*; // for static metamodels
import com.thirdcc.webapp.domain.ImageStorage;
import com.thirdcc.webapp.repository.ImageStorageRepository;
import com.thirdcc.webapp.service.criteria.ImageStorageCriteria;
import com.thirdcc.webapp.service.dto.ImageStorageDTO;
import com.thirdcc.webapp.service.mapper.ImageStorageMapper;
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
 * Service for executing complex queries for {@link ImageStorage} entities in the database.
 * The main input is a {@link ImageStorageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ImageStorageDTO} or a {@link Page} of {@link ImageStorageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ImageStorageQueryService extends QueryService<ImageStorage> {

  private final Logger log = LoggerFactory.getLogger(ImageStorageQueryService.class);

  private final ImageStorageRepository imageStorageRepository;

  private final ImageStorageMapper imageStorageMapper;

  public ImageStorageQueryService(ImageStorageRepository imageStorageRepository, ImageStorageMapper imageStorageMapper) {
    this.imageStorageRepository = imageStorageRepository;
    this.imageStorageMapper = imageStorageMapper;
  }

  /**
   * Return a {@link List} of {@link ImageStorageDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public List<ImageStorageDTO> findByCriteria(ImageStorageCriteria criteria) {
    log.debug("find by criteria : {}", criteria);
    final Specification<ImageStorage> specification = createSpecification(criteria);
    return imageStorageMapper.toDto(imageStorageRepository.findAll(specification));
  }

  /**
   * Return a {@link Page} of {@link ImageStorageDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<ImageStorageDTO> findByCriteria(ImageStorageCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<ImageStorage> specification = createSpecification(criteria);
    return imageStorageRepository.findAll(specification, page).map(imageStorageMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(ImageStorageCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<ImageStorage> specification = createSpecification(criteria);
    return imageStorageRepository.count(specification);
  }

  /**
   * Function to convert {@link ImageStorageCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<ImageStorage> createSpecification(ImageStorageCriteria criteria) {
    Specification<ImageStorage> specification = Specification.where(null);
    if (criteria != null) {
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), ImageStorage_.id));
      }
      if (criteria.getImageUrl() != null) {
        specification = specification.and(buildStringSpecification(criteria.getImageUrl(), ImageStorage_.imageUrl));
      }
      if (criteria.getFileName() != null) {
        specification = specification.and(buildStringSpecification(criteria.getFileName(), ImageStorage_.fileName));
      }
      if (criteria.getFileType() != null) {
        specification = specification.and(buildStringSpecification(criteria.getFileType(), ImageStorage_.fileType));
      }
    }
    return specification;
  }
}
