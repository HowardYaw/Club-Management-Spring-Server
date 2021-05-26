package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.*; // for static metamodels
import com.thirdcc.webapp.domain.CourseProgram;
import com.thirdcc.webapp.repository.CourseProgramRepository;
import com.thirdcc.webapp.service.criteria.CourseProgramCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CourseProgramQueryService extends QueryService<CourseProgram> {

    private final Logger log = LoggerFactory.getLogger(CourseProgramQueryService.class);

    private final CourseProgramRepository courseProgramRepository;

    public CourseProgramQueryService(CourseProgramRepository courseProgramRepository) {
        this.courseProgramRepository = courseProgramRepository;
    }

    @Transactional(readOnly = true)
    public List<CourseProgram> findByCriteria(CourseProgramCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<CourseProgram> specification = createSpecification(criteria);
        return courseProgramRepository.findAll(specification);
    }

    @Transactional(readOnly = true)
    public Page<CourseProgram> findByCriteria(CourseProgramCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CourseProgram> specification = createSpecification(criteria);
        return courseProgramRepository.findAll(specification, page);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(CourseProgramCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<CourseProgram> specification = createSpecification(criteria);
        return courseProgramRepository.count(specification);
    }

    /**
     * Function to convert {@link CourseProgramCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CourseProgram> createSpecification(CourseProgramCriteria criteria) {
        Specification<CourseProgram> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), CourseProgram_.id));
            }
            if (criteria.getFacultyId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFacultyId(), CourseProgram_.facultyId));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), CourseProgram_.name));
            }
            if (criteria.getNumOfSem() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNumOfSem(), CourseProgram_.numOfSem));
            }
        }
        return specification;
    }
}
