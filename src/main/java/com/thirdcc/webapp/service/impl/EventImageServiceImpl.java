package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.service.EventImageService;
import com.thirdcc.webapp.domain.EventImage;
import com.thirdcc.webapp.repository.EventImageRepository;
import com.thirdcc.webapp.service.dto.EventImageDTO;
import com.thirdcc.webapp.service.mapper.EventImageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link EventImage}.
 */
@Service
@Transactional
public class EventImageServiceImpl implements EventImageService {

    private final Logger log = LoggerFactory.getLogger(EventImageServiceImpl.class);

    private final EventImageRepository eventImageRepository;

    private final EventImageMapper eventImageMapper;

    public EventImageServiceImpl(EventImageRepository eventImageRepository, EventImageMapper eventImageMapper) {
        this.eventImageRepository = eventImageRepository;
        this.eventImageMapper = eventImageMapper;
    }

    /**
     * Save a eventImage.
     *
     * @param eventImageDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EventImageDTO save(EventImageDTO eventImageDTO) {
        log.debug("Request to save EventImage : {}", eventImageDTO);
        EventImage eventImage = eventImageMapper.toEntity(eventImageDTO);
        eventImage = eventImageRepository.save(eventImage);
        return eventImageMapper.toDto(eventImage);
    }

    /**
     * Get all the eventImages.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventImageDTO> findAll() {
        log.debug("Request to get all EventImages");
        return eventImageRepository.findAll().stream()
            .map(eventImageMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one eventImage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EventImageDTO> findOne(Long id) {
        log.debug("Request to get EventImage : {}", id);
        return eventImageRepository.findById(id)
            .map(eventImageMapper::toDto);
    }

    /**
     * Delete the eventImage by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete EventImage : {}", id);
        eventImageRepository.deleteById(id);
    }
}
