package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.service.EventService;
import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.service.dto.EventDTO;
import com.thirdcc.webapp.service.mapper.EventMapper;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
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
 * Service Implementation for managing {@link Event}.
 */
@Service
@Transactional
public class EventServiceImpl implements EventService {

    private static final String ENTITY_NAME = "event";

    private final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    /**
     * Save a event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EventDTO save(EventDTO eventDTO) {
        log.debug("Request to save Event : {}", eventDTO);
        if (eventDTO.getName().isEmpty()){
            throw new BadRequestAlertException("Invalid Parameters", ENTITY_NAME, "noname");
        }
        Event event = eventMapper.toEntity(eventDTO);
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    /**
     * Get all the events.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Events");
        return eventRepository.findAll(pageable)
            .map(eventMapper::toDto);
    }

    @Override
    public Page<EventDTO> findAllByDateRange(Pageable pageable, String fromDate, String toDate) {
        log.debug("Request to get all Events and filter by date");

        Instant from = Instant.parse(fromDate);
        Instant to = Instant.parse(toDate);
        return eventRepository.findEventsByStartDateBetween(from, to, pageable)
            .map(eventMapper::toDto);
    }


    /**
     * Get one event by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EventDTO> findOne(Long id) {
        log.debug("Request to get Event : {}", id);
        return eventRepository.findById(id)
            .map(eventMapper::toDto);
    }

    /**
     * Delete the event by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Event : {}", id);
        eventRepository.deleteById(id);
    }

    /**
     * Find the not cancelled event by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public Event findEventByIdAndNotCancelledStatus(Long id) {
        Set<EventStatus> eventStatuses = new HashSet<EventStatus>() {{
            add(EventStatus.OPEN);
            add(EventStatus.POSTPONED);
        }};
        return eventRepository.findOneByIdAndStatusIn(id, eventStatuses)
            .orElseThrow(() -> new BadRequestException("Event not found, might be cancelled or not exist"));
    }

    /**
     * Cancel event by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public EventDTO cancelEventById(Long id) {
        log.debug("Request to cancel Event : {}", id);
        Set<EventStatus> eventStatuses = new HashSet<EventStatus>() {{
            add(EventStatus.OPEN);
            add(EventStatus.POSTPONED);
        }};
        Event event = eventRepository
            .findOneByIdAndStatusIn(id, eventStatuses)
            .orElseThrow(() -> new BadRequestException("This event does not exists or it is not happening"));

        event.setStatus(EventStatus.CANCELLED);
        return eventMapper.toDto(event);
    }

}
