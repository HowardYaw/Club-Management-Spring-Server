package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.domain.EventChecklist;
import com.thirdcc.webapp.domain.enumeration.EventChecklistStatus;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.service.EventChecklistService;
import com.thirdcc.webapp.repository.EventChecklistRepository;
import com.thirdcc.webapp.service.EventService;
import com.thirdcc.webapp.service.dto.EventChecklistDTO;
import com.thirdcc.webapp.service.mapper.EventChecklistMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Service Implementation for managing {@link EventChecklist}.
 */
@Service
@Transactional
public class EventChecklistServiceImpl implements EventChecklistService {

    private final Logger log = LoggerFactory.getLogger(EventChecklistServiceImpl.class);

    private final EventChecklistRepository checklistRepository;

    private final EventChecklistMapper checklistMapper;

    private final EventService eventService;

    public EventChecklistServiceImpl(
        EventChecklistRepository checklistRepository,
        EventChecklistMapper checklistMapper,
        EventService eventService
    ) {
        this.checklistRepository = checklistRepository;
        this.checklistMapper = checklistMapper;
        this.eventService = eventService;
    }

    /**
     * Save a checklist.
     *
     * @param eventChecklistDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EventChecklistDTO save(EventChecklistDTO eventChecklistDTO) {
        log.debug("Request to save Checklist : {}", eventChecklistDTO);
        Event event = eventService
            .findEventByIdAndNotCancelledStatus(eventChecklistDTO.getEventId());
        if (event.getStartDate().isBefore(Instant.now())) {
            throw new BadRequestException("Event is started, cannot create checklist for this event");
        }
        EventChecklist eventChecklist = checklistMapper.toEntity(eventChecklistDTO);
        eventChecklist.setStatus(EventChecklistStatus.OPEN);
        eventChecklist = checklistRepository.save(eventChecklist);
        return checklistMapper.toDto(eventChecklist);
    }

    @Override
    public EventChecklistDTO update(EventChecklistDTO eventChecklistDTO) {
        log.debug("Request to update Event Checklist: {}", eventChecklistDTO);
        EventChecklist eventChecklist = checklistRepository.findById(eventChecklistDTO.getId())
            .orElseThrow(() -> new BadRequestException("Event Checklist not found"));
        Event event = eventService
            .findEventByIdAndNotCancelledStatus(eventChecklist.getEventId());
        if (event.getStartDate().isBefore(Instant.now())) {
            throw new BadRequestException("Event is started, cannot create checklist for this event");
        }
        if (eventChecklist.getStatus().equals(EventChecklistStatus.FINISHED)) {
            throw new BadRequestException("Event Checklist is completed, not allow to update");
        }
        EventChecklist updatedEventChecklist = checklistMapper.toEntity(eventChecklistDTO);
        return checklistMapper.toDto(
            checklistRepository.save(updatedEventChecklist)
        );
    }

    /**
     * Get all the checklists.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventChecklistDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Checklists");
        return checklistRepository.findAll(pageable)
            .map(checklistMapper::toDto);
    }


    /**
     * Get one checklist by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EventChecklistDTO> findOne(Long id) {
        log.debug("Request to get Checklist : {}", id);
        return checklistRepository.findById(id)
            .map(checklistMapper::toDto)
            .map(this::mapEventName);
    }

    /**
     * Delete the checklist by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Checklist : {}", id);
        EventChecklist eventChecklist = checklistRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("Event Checklist not found: "+id));
        Event event = eventService.findEventByIdAndNotCancelledStatus(eventChecklist.getEventId());
        if (event.getStartDate().isBefore(Instant.now())) {
            throw new BadRequestException("Cannot delete a event checklist for event that had been start");
        }
        if (eventChecklist.getStatus() != EventChecklistStatus.OPEN) {
            throw new BadRequestException("Cannot delete a event Checklist that is not open status");
        }
        checklistRepository.deleteById(id);
    }

    @Override
    public EventChecklistDTO updateStatus(Long id, EventChecklistStatus eventChecklistStatus) {
        log.debug("Request to update status of event checklist: {}, to {}", id, eventChecklistStatus);
        EventChecklist eventChecklist = checklistRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("Event Checklist not exists: " + id));
        Event event = eventService.findEventByIdAndNotCancelledStatus(eventChecklist.getEventId());
        eventChecklist.setStatus(eventChecklistStatus);
        return checklistMapper.toDto(
            checklistRepository.save(eventChecklist)
        );
    }

    @Override
    public Page<EventChecklistDTO> findAllByEventId(Long eventId, Pageable pageable) {
        eventService.findEventByIdAndNotCancelledStatus(eventId);
        return checklistRepository.findAllByEventId(eventId, pageable)
            .map(checklistMapper::toDto);
    }

    private EventChecklistDTO mapEventName(EventChecklistDTO eventChecklistDTO) {
        Event event = eventService.findEventByIdAndNotCancelledStatus(eventChecklistDTO.getEventId());
        eventChecklistDTO.setEventName(event.getName());
        return eventChecklistDTO;
    }
}
