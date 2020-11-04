package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.domain.EventChecklist;
import com.thirdcc.webapp.domain.enumeration.EventChecklistStatus;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.service.EventChecklistService;
import com.thirdcc.webapp.repository.EventChecklistRepository;
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

    private final EventRepository eventRepository;

    public EventChecklistServiceImpl(
        EventChecklistRepository checklistRepository,
        EventChecklistMapper checklistMapper,
        EventRepository eventRepository
    ) {
        this.checklistRepository = checklistRepository;
        this.checklistMapper = checklistMapper;
        this.eventRepository = eventRepository;
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
        // TODO: change to eventService done by KC
        Event event = eventRepository
            .findById(eventChecklistDTO.getEventId())
            .orElseThrow(() -> new BadRequestException("Event not found"));
        if (event.getStartDate().isBefore(Instant.now())) {
            throw new BadRequestException("Event is started, cannot create checklist for this event");
        }
        EventChecklist eventChecklist = checklistMapper.toEntity(eventChecklistDTO);
        eventChecklist.setStatus(EventChecklistStatus.OPEN);
        eventChecklist = checklistRepository.save(eventChecklist);
        return checklistMapper.toDto(eventChecklist);
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
            .map(checklistMapper::toDto);
    }

    /**
     * Delete the checklist by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Checklist : {}", id);
        checklistRepository.deleteById(id);
    }
}
