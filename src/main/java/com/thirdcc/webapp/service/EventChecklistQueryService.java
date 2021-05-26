package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.*; // for static metamodels
import com.thirdcc.webapp.domain.EventChecklist;
import com.thirdcc.webapp.repository.EventChecklistRepository;
import com.thirdcc.webapp.service.criteria.EventChecklistCriteria;
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
public class EventChecklistQueryService extends QueryService<EventChecklist> {

    private final Logger log = LoggerFactory.getLogger(EventChecklistQueryService.class);

    private final EventChecklistRepository eventChecklistRepository;

    public EventChecklistQueryService(EventChecklistRepository eventChecklistRepository) {
        this.eventChecklistRepository = eventChecklistRepository;
    }

    @Transactional(readOnly = true)
    public List<EventChecklist> findByCriteria(EventChecklistCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<EventChecklist> specification = createSpecification(criteria);
        return eventChecklistRepository.findAll(specification);
    }

    @Transactional(readOnly = true)
    public Page<EventChecklist> findByCriteria(EventChecklistCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<EventChecklist> specification = createSpecification(criteria);
        return eventChecklistRepository.findAll(specification, page);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(EventChecklistCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<EventChecklist> specification = createSpecification(criteria);
        return eventChecklistRepository.count(specification);
    }

    /**
     * Function to convert {@link EventChecklistCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<EventChecklist> createSpecification(EventChecklistCriteria criteria) {
        Specification<EventChecklist> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), EventChecklist_.id));
            }
            if (criteria.getEventId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEventId(), EventChecklist_.eventId));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), EventChecklist_.name));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), EventChecklist_.status));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), EventChecklist_.type));
            }
        }
        return specification;
    }
}
