package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.service.EventActivityService;
import com.thirdcc.webapp.domain.EventActivity;
import com.thirdcc.webapp.repository.EventActivityRepository;
import com.thirdcc.webapp.service.EventService;
import com.thirdcc.webapp.service.dto.EventActivityDTO;
import com.thirdcc.webapp.service.mapper.EventActivityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Service Implementation for managing {@link EventActivity}.
 */
@Service
@Transactional
public class EventActivityServiceImpl implements EventActivityService {

    private final Logger log = LoggerFactory.getLogger(EventActivityServiceImpl.class);

    private final EventActivityRepository eventActivityRepository;

    private final EventService eventService;

    private final EventActivityMapper eventActivityMapper;

    public EventActivityServiceImpl(
        EventActivityRepository eventActivityRepository,
        EventService eventService, EventActivityMapper eventActivityMapper
    ) {
        this.eventActivityRepository = eventActivityRepository;
        this.eventService = eventService;
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
        Event event = eventService
            .findEventByIdAndNotCancelledStatus(eventActivityDTO.getEventId());
        if (event.getEndDate().isBefore(Instant.now())) {
            throw new BadRequestException("cannot save eventActivity for ended event");
        }
        if (eventActivityDTO.getStartDate().isBefore(Instant.now())) {
            throw new BadRequestException("event activity start date cannot be earlier than today");
        }
        if (eventActivityDTO.getStartDate().isAfter(event.getEndDate())) {
            throw new BadRequestException("event activity start date cannot be later than event end date");
        }
        EventActivity eventActivity = eventActivityMapper.toEntity(eventActivityDTO);
        eventActivity = eventActivityRepository.save(eventActivity);
        return eventActivityMapper.toDto(eventActivity);
    }

    @Override
    public EventActivityDTO update(EventActivityDTO eventActivityDTO) {
        EventActivity eventActivity = eventActivityRepository
            .findById(eventActivityDTO.getId())
            .orElseThrow(() -> new BadRequestException("eventActivity does not exist"));
        if (!eventActivityDTO.getEventId().equals(eventActivity.getEventId())) {
            throw new BadRequestException("Cannot update eventId of Event Activity");
        }
        Event event = eventService.findEventByIdAndNotCancelledStatus(eventActivityDTO.getEventId());
        if (event.getEndDate().isBefore(Instant.now())) {
            throw new BadRequestException("cannot save eventActivity for ended event");
        }
        if (eventActivityDTO.getStartDate().isBefore(Instant.now())) {
            throw new BadRequestException("event activity start date cannot be earlier than today");
        }
        if (eventActivityDTO.getStartDate().isAfter(event.getEndDate())) {
            throw new BadRequestException("event activity start date cannot be later than event end date");
        }
        eventActivity.setName(eventActivityDTO.getName());
        eventActivity.setDescription(eventActivityDTO.getDescription());
        eventActivity.setStartDate(eventActivityDTO.getStartDate());
        eventActivity.setDurationInDay(eventActivityDTO.getDurationInDay());
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

    @Override
    @Transactional(readOnly = true)
    public Page<EventActivityDTO> findAllByEventId(Pageable pageable, Long eventId) {
        return eventActivityRepository.findAllByEventId(pageable, eventId)
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
        EventActivity eventActivity = eventActivityRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestException("eventActivity does not exist"));
        Event event = eventService
            .findEventByIdAndNotCancelledStatus(eventActivity.getEventId());
        if (event.getEndDate().isBefore(Instant.now())) {
            throw new BadRequestException("cannot delete eventActivity for ended event");
        }
        eventActivityRepository.deleteById(id);
    }
}
