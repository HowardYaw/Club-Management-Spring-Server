package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.service.EventActivityService;
import com.thirdcc.webapp.domain.EventActivity;
import com.thirdcc.webapp.repository.EventActivityRepository;
import com.thirdcc.webapp.service.dto.EventActivityDTO;
import com.thirdcc.webapp.service.mapper.EventActivityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link EventActivity}.
 */
@Service
@Transactional
public class EventActivityServiceImpl implements EventActivityService {

    private final Logger log = LoggerFactory.getLogger(EventActivityServiceImpl.class);

    private final EventActivityRepository eventActivityRepository;

    private final EventActivityMapper eventActivityMapper;

    public EventActivityServiceImpl(EventActivityRepository eventActivityRepository, EventActivityMapper eventActivityMapper) {
        this.eventActivityRepository = eventActivityRepository;
        this.eventActivityMapper = eventActivityMapper;
    }

    /**
     * Save a eventActivity.
     *
     * @param eventActivityDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EventActivityDTO save(EventActivityDTO eventActivityDTO) {
        log.debug("Request to save EventActivity : {}", eventActivityDTO);
        EventActivity eventActivity = eventActivityMapper.toEntity(eventActivityDTO);
        eventActivity = eventActivityRepository.save(eventActivity);
        return eventActivityMapper.toDto(eventActivity);
    }

    /**
     * Get all the eventActivities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventActivityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all EventActivities");
        return eventActivityRepository.findAll(pageable)
            .map(eventActivityMapper::toDto);
    }


    /**
     * Get one eventActivity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EventActivityDTO> findOne(Long id) {
        log.debug("Request to get EventActivity : {}", id);
        return eventActivityRepository.findById(id)
            .map(eventActivityMapper::toDto);
    }

    /**
     * Delete the eventActivity by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete EventActivity : {}", id);
        eventActivityRepository.deleteById(id);
    }
}
