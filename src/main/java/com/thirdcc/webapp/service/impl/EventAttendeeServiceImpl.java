package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.repository.UserRepository;
import com.thirdcc.webapp.service.EventAttendeeService;
import com.thirdcc.webapp.domain.EventAttendee;
import com.thirdcc.webapp.repository.EventAttendeeRepository;
import com.thirdcc.webapp.service.dto.EventAttendeeDTO;
import com.thirdcc.webapp.service.mapper.EventAttendeeMapper;
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
 * Service Implementation for managing {@link EventAttendee}.
 */
@Service
@Transactional
public class EventAttendeeServiceImpl implements EventAttendeeService {

    private final Logger log = LoggerFactory.getLogger(EventAttendeeServiceImpl.class);

    private final EventAttendeeRepository eventAttendeeRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final EventAttendeeMapper eventAttendeeMapper;

    public EventAttendeeServiceImpl(EventAttendeeRepository eventAttendeeRepository, EventRepository eventRepository, UserRepository userRepository, EventAttendeeMapper eventAttendeeMapper) {
        this.eventAttendeeRepository = eventAttendeeRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventAttendeeMapper = eventAttendeeMapper;
    }

    /**
     * Save a eventAttendee.
     *
     * @param eventAttendeeDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EventAttendeeDTO save(EventAttendeeDTO eventAttendeeDTO) {
        log.debug("Request to save EventAttendee : {}", eventAttendeeDTO);

        Set<EventStatus> eventStatuses = new HashSet<EventStatus>() {{
            add(EventStatus.OPEN);
            add(EventStatus.POSTPONED);
        }};

        userRepository
            .findById(eventAttendeeDTO.getUserId())
            .orElseThrow(() -> new BadRequestException("User not found"));

        Event event = eventRepository
            .findOneByIdAndStatusIn(eventAttendeeDTO.getEventId(), eventStatuses)
            .orElseThrow(()-> new BadRequestException("This event does not exists or it is not happening"));

        if(event.getEndDate().isBefore(Instant.now())){
            throw new BadRequestException("Cannot add attendee to ended event");
        }

        boolean eventAttendeeExisted = eventAttendeeRepository
            .findOneByEventIdAndUserId(eventAttendeeDTO.getEventId(),eventAttendeeDTO.getUserId())
            .isPresent();
        log.debug("eventAttendeeExisted:"+ eventAttendeeExisted);

        if(eventAttendeeExisted){
            throw new BadRequestException("User has registered as attendee for this event");
        }

        EventAttendee eventAttendee = eventAttendeeMapper.toEntity(eventAttendeeDTO);
        eventAttendee = eventAttendeeRepository.save(eventAttendee);
        return eventAttendeeMapper.toDto(eventAttendee);

    }

    /**
     * Get all the eventAttendees.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventAttendeeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all EventAttendees");
        return eventAttendeeRepository.findAll(pageable)
            .map(eventAttendeeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventAttendeeDTO> findAllByEventId(Pageable pageable, Long eventId) {
        log.debug("Request to get all EventAttendee by Event Id: {}", eventId);
        eventRepository
            .findById(eventId)
            .orElseThrow(() -> new BadRequestException("Event not found"));

        return eventAttendeeRepository.findAllByEventId(pageable, eventId)
            .map(eventAttendeeMapper::toDto);
    }

    /**
     * Get one eventAttendee by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EventAttendeeDTO> findOne(Long id) {
        log.debug("Request to get EventAttendee : {}", id);
        return eventAttendeeRepository.findById(id)
            .map(eventAttendeeMapper::toDto);
    }

    /**
     * Delete the eventAttendee by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete EventAttendee : {}", id);
        eventAttendeeRepository.deleteById(id);
    }
}
