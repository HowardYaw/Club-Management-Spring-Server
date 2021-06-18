package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.domain.enumeration.EventStatus;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.service.EventService;
import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.service.ImageStorageService;
import com.thirdcc.webapp.service.dto.EventDTO;
import com.thirdcc.webapp.service.dto.ImageStorageDTO;
import com.thirdcc.webapp.service.mapper.EventMapper;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    private final ImageStorageService imageStorageService;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper, ImageStorageService imageStorageService) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.imageStorageService = imageStorageService;
    }

    /**
     * Save a event.
     *
     * @param eventDTO the entity to save.
     * @param multipartFile Event Image File
     * @return the persisted entity.
     */
    @Override
    public EventDTO save(EventDTO eventDTO, MultipartFile multipartFile) {
        log.debug("Request to save Event : {}", eventDTO);
        log.debug("Event Image File: {}", multipartFile);
        if (eventDTO.getName().isEmpty()){
            throw new BadRequestAlertException("Invalid Parameters", ENTITY_NAME, "noname");
        }

        if (multipartFile != null && !multipartFile.isEmpty()) {
            ImageStorageDTO eventImageStorage = uploadEventImage(multipartFile, new ImageStorageDTO());
            eventDTO.setImageStorageId(eventImageStorage.getId());
        }

        Event event = eventMapper.toEntity(eventDTO);
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    /**
     * Update a event.
     *
     * @param eventDTO the entity to update.
     * @param multipartFile Event Image File
     * @return the persisted entity.
     */
    @Override
    public EventDTO update(EventDTO eventDTO, MultipartFile multipartFile) {
        log.debug("Request to update Event : {}", eventDTO);
        Event existingEvent = eventRepository.findById(eventDTO.getId())
            .orElseThrow(() -> new BadRequestException("No event with Id found for update"));
        if (eventDTO.getName().isEmpty()){
            throw new BadRequestAlertException("Invalid Parameters", ENTITY_NAME, "noname");
        }

        if (multipartFile != null && !multipartFile.isEmpty()) {
            ImageStorageDTO originalImageStorage = imageStorageService.findOne(existingEvent .getImageStorageId())
                .orElse(new ImageStorageDTO());
            ImageStorageDTO eventImageStorage = uploadEventImage(multipartFile, originalImageStorage);
            eventDTO.setImageStorageId(eventImageStorage.getId());
        }

        Event event = eventMapper.toEntity(eventDTO);
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    private ImageStorageDTO uploadEventImage(MultipartFile multipartFile, ImageStorageDTO imageStorageDTO) {
        try {
            imageStorageDTO = imageStorageService.save(imageStorageDTO, multipartFile);
        } catch (IOException e) {
            log.error("Exception during Upload Image: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
        return imageStorageDTO;
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
            .map(eventMapper::toDto)
            .map(this::mapEventImageStorage);
    }

    @Override
    public Page<EventDTO> findAllByDateRange(Pageable pageable, String fromDate, String toDate) {
        log.debug("Request to get all Events and filter by date");

        Instant from = Instant.parse(fromDate);
        Instant to = Instant.parse(toDate);
        return eventRepository.findEventsByStartDateBetween(from, to, pageable)
            .map(eventMapper::toDto)
            .map(this::mapEventImageStorage);
    }

    /**
     * Get all the upcoming events.
     *
     * @return the list of entity.
     */
    @Override
    public Page<EventDTO> findAllUpcomingEvents(Pageable pageable) {
        log.debug("Request to get all Upcoming Events");

        Set<EventStatus> eventStatuses = new HashSet<EventStatus>() {{
            add(EventStatus.OPEN);
            add(EventStatus.POSTPONED);
        }};

        Instant from = Instant.now();

        return eventRepository.findEventsByStartDateAfterAndStatusIn(from, eventStatuses, pageable)
            .map(eventMapper::toDto)
            .map(this::mapEventImageStorage);
    }

    /**
     * Get all the past events.
     *
     * @return the list of entity.
     */
    @Override
    public Page<EventDTO> findAllPastEvents(Pageable pageable) {
        log.debug("Request to get all Past Events");

        Set<EventStatus> eventStatuses = new HashSet<EventStatus>() {{
            add(EventStatus.OPEN);
            add(EventStatus.POSTPONED);
        }};

        Instant from = Instant.now();

        return eventRepository.findEventsByStartDateBeforeAndStatusIn(from, eventStatuses, pageable)
            .map(eventMapper::toDto)
            .map(this::mapEventImageStorage);
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
            .map(eventMapper::toDto)
            .map(this::mapEventImageStorage);
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

    private EventDTO mapEventImageStorage(EventDTO eventDTO) {
        if (eventDTO.getImageStorageId() != null) {
            imageStorageService.findOne(eventDTO.getImageStorageId())
                .ifPresent(eventDTO::setImageStorageDTO);
        }
        return eventDTO;
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
