package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.service.EventRegistrationClosingCriteriaService;
import com.thirdcc.webapp.domain.EventRegistrationClosingCriteria;
import com.thirdcc.webapp.repository.EventRegistrationClosingCriteriaRepository;
import com.thirdcc.webapp.service.dto.EventRegistrationClosingCriteriaDTO;
import com.thirdcc.webapp.service.mapper.EventRegistrationClosingCriteriaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link EventRegistrationClosingCriteria}.
 */
@Service
@Transactional
public class EventRegistrationClosingCriteriaServiceImpl implements EventRegistrationClosingCriteriaService {

    private final Logger log = LoggerFactory.getLogger(EventRegistrationClosingCriteriaServiceImpl.class);

    private final EventRegistrationClosingCriteriaRepository eventRegistrationClosingCriteriaRepository;

    private final EventRegistrationClosingCriteriaMapper eventRegistrationClosingCriteriaMapper;

    public EventRegistrationClosingCriteriaServiceImpl(EventRegistrationClosingCriteriaRepository eventRegistrationClosingCriteriaRepository, EventRegistrationClosingCriteriaMapper eventRegistrationClosingCriteriaMapper) {
        this.eventRegistrationClosingCriteriaRepository = eventRegistrationClosingCriteriaRepository;
        this.eventRegistrationClosingCriteriaMapper = eventRegistrationClosingCriteriaMapper;
    }

    /**
     * Save a eventRegistrationClosingCriteria.
     *
     * @param eventRegistrationClosingCriteriaDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EventRegistrationClosingCriteriaDTO save(EventRegistrationClosingCriteriaDTO eventRegistrationClosingCriteriaDTO) {
        log.debug("Request to save EventRegistrationClosingCriteria : {}", eventRegistrationClosingCriteriaDTO);
        EventRegistrationClosingCriteria eventRegistrationClosingCriteria = eventRegistrationClosingCriteriaMapper.toEntity(eventRegistrationClosingCriteriaDTO);
        eventRegistrationClosingCriteria = eventRegistrationClosingCriteriaRepository.save(eventRegistrationClosingCriteria);
        return eventRegistrationClosingCriteriaMapper.toDto(eventRegistrationClosingCriteria);
    }

    /**
     * Get all the eventRegistrationClosingCriteria.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventRegistrationClosingCriteriaDTO> findAll() {
        log.debug("Request to get all EventRegistrationClosingCriteria");
        return eventRegistrationClosingCriteriaRepository.findAll().stream()
            .map(eventRegistrationClosingCriteriaMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one eventRegistrationClosingCriteria by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EventRegistrationClosingCriteriaDTO> findOne(Long id) {
        log.debug("Request to get EventRegistrationClosingCriteria : {}", id);
        return eventRegistrationClosingCriteriaRepository.findById(id)
            .map(eventRegistrationClosingCriteriaMapper::toDto);
    }

    /**
     * Delete the eventRegistrationClosingCriteria by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete EventRegistrationClosingCriteria : {}", id);
        eventRegistrationClosingCriteriaRepository.deleteById(id);
    }
}
