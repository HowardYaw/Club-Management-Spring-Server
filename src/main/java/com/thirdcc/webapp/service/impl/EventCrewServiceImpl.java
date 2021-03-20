package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.domain.User;
import com.thirdcc.webapp.repository.UserRepository;
import com.thirdcc.webapp.service.EventCrewService;
import com.thirdcc.webapp.domain.EventCrew;
import com.thirdcc.webapp.repository.EventCrewRepository;
import com.thirdcc.webapp.service.EventService;
import com.thirdcc.webapp.service.dto.EventCrewDTO;
import com.thirdcc.webapp.service.mapper.EventCrewMapper;
import com.thirdcc.webapp.utils.PageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link EventCrew}.
 */
@Service
@Transactional
public class EventCrewServiceImpl implements EventCrewService {

    private final Logger log = LoggerFactory.getLogger(EventCrewServiceImpl.class);

    private final EventCrewRepository eventCrewRepository;

    private final UserRepository userRepository;

    private final EventCrewMapper eventCrewMapper;

    private final EventService eventService;

    public EventCrewServiceImpl(EventCrewRepository eventCrewRepository, UserRepository userRepository, EventCrewMapper eventCrewMapper, EventService eventService) {
        this.eventCrewRepository = eventCrewRepository;
        this.userRepository = userRepository;
        this.eventCrewMapper = eventCrewMapper;
        this.eventService = eventService;
    }

    /**
     * Save a eventCrew.
     *
     * @param eventCrewDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EventCrewDTO save(EventCrewDTO eventCrewDTO) {
        log.debug("Request to save EventCrew : {}", eventCrewDTO);
        EventCrew eventCrew = eventCrewMapper.toEntity(eventCrewDTO);
        eventCrew = eventCrewRepository.save(eventCrew);
        return eventCrewMapper.toDto(eventCrew);
    }

    /**
     * Get all the eventCrews.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventCrewDTO> findAll() {
        log.debug("Request to get all EventCrews");
        return eventCrewRepository.findAll().stream()
            .map(eventCrewMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the eventCrews.
     *
     *
     * @param eventId the id of the event entity.
     * @return the list of entities.
     */
    @Override
    @Transactional
    public Page<EventCrewDTO> findAllByEventId(Pageable pageable, Long eventId){
        log.debug("Request to get all EventCrews with event Id : {}", eventId);

        List<EventCrewDTO> eventCrewDTOList = eventCrewRepository.findAllByEventId(pageable, eventId)
            .stream()
            .map(eventCrewMapper::toDto)
            .map(this::mapUserDetails)
            .collect(Collectors.toList());

        return PageUtils.toPage(eventCrewDTOList, pageable);
    }

    /**
     * Get one eventCrew by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EventCrewDTO> findOne(Long id) {
        log.debug("Request to get EventCrew : {}", id);
        return eventCrewRepository.findById(id)
            .map(eventCrewMapper::toDto);
    }

    /**
     * Delete the eventCrew by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete EventCrew : {}", id);
        eventCrewRepository.deleteById(id);
    }

    private EventCrewDTO mapUserDetails(EventCrewDTO eventCrewDTO) {
        User user = userRepository.findOneById(eventCrewDTO.getUserId());
        eventCrewDTO.setName(user.getFirstName() + " " + user.getLastName() );
        return eventCrewDTO;
    }
}
