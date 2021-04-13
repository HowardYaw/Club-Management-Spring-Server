package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.repository.EventRepository;
import com.thirdcc.webapp.repository.UserRepository;
import com.thirdcc.webapp.repository.UserUniInfoRepository;
import com.thirdcc.webapp.service.EventAttendeeService;
import com.thirdcc.webapp.domain.EventAttendee;
import com.thirdcc.webapp.domain.User;
import com.thirdcc.webapp.domain.UserUniInfo;
import com.thirdcc.webapp.repository.EventAttendeeRepository;
import com.thirdcc.webapp.service.EventService;
import com.thirdcc.webapp.service.dto.EventAttendeeDTO;
import com.thirdcc.webapp.service.mapper.EventAttendeeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

/**
 * Service Implementation for managing {@link EventAttendee}.
 */
@Service
@Transactional
public class EventAttendeeServiceImpl implements EventAttendeeService {

    private final Logger log = LoggerFactory.getLogger(EventAttendeeServiceImpl.class);

    private final EventAttendeeRepository eventAttendeeRepository;

    private final EventRepository eventRepository;

    private final EventService eventService;

    private final UserRepository userRepository;
    
    private final UserUniInfoRepository userUniInfoRepository;

    private final EventAttendeeMapper eventAttendeeMapper;

    public EventAttendeeServiceImpl(EventAttendeeRepository eventAttendeeRepository, EventRepository eventRepository, EventService eventService, UserRepository userRepository, UserUniInfoRepository userUniInfoRepository, EventAttendeeMapper eventAttendeeMapper) {
        this.eventAttendeeRepository = eventAttendeeRepository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.userRepository = userRepository;
        this.userUniInfoRepository = userUniInfoRepository;
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
        userRepository
            .findById(eventAttendeeDTO.getUserId())
            .orElseThrow(() -> new BadRequestException("User not found"));
        Event event = eventService
            .findEventByIdAndNotCancelledStatus(eventAttendeeDTO.getEventId());

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
    
    /**
     * Get all the eventAttendees from an Event via Event Id.
     *
     * @param pageable the pagination information.
     * @param eventId the eventId of the event
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventAttendeeDTO> findAllByEventId(Pageable pageable, Long eventId) {
        log.debug("Request to get all EventAttendee by Event Id: {}", eventId);
        eventRepository
            .findById(eventId)
            .orElseThrow(() -> new BadRequestException("Event not found"));
        List<EventAttendeeDTO> list = eventAttendeeRepository.findAllByEventId(eventId)
                .stream()
                .map(eventAttendeeMapper::toDto)
                .map(this::mapEventAttendeeDetails)
                .collect(Collectors.toList());
        String orderProperty = null;
        Direction orderDirection = null;
        for(Sort.Order order : pageable.getSort()){
            orderProperty = order.getProperty();
            orderDirection = order.getDirection();
        }
        if(null == orderProperty){
            list.sort((EventAttendeeDTO e1, EventAttendeeDTO e2) -> {
                return Long.compare(e1.getId(), e2.getId());
            });
        }
        else if(orderProperty.equals("provideTransport")){
            if(null == orderDirection || orderDirection.isAscending()){
                list.sort((EventAttendeeDTO e1, EventAttendeeDTO e2) -> {
                    return Boolean.compare(e1.isProvideTransport(), e2.isProvideTransport());
                });
            }
            else{
                list.sort((EventAttendeeDTO e1, EventAttendeeDTO e2) -> {
                    return Boolean.compare(e2.isProvideTransport(), e1.isProvideTransport());
                });
            }
        }
        else if(orderProperty.equals("yearSession")){
            if(null == orderDirection || orderDirection.isAscending()){
                list.sort((EventAttendeeDTO e1, EventAttendeeDTO e2) -> {
                    return e1.getYearSession().compareTo(e2.getYearSession());
                });
            }
            else{
                list.sort((EventAttendeeDTO e1, EventAttendeeDTO e2) -> {
                    return e2.getYearSession().compareTo(e1.getYearSession());
                });
            }
        }
        else{
            list.sort((EventAttendeeDTO e1, EventAttendeeDTO e2) -> {
                return Long.compare(e1.getId(), e2.getId());
            });
        }
        // refer to https://stackoverflow.com/questions/37136679/how-to-convert-a-list-of-enity-object-to-page-object-in-spring-mvc-jpa
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl(list.subList(start, end), pageable, list.size());
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
    
    private EventAttendeeDTO mapEventAttendeeDetails(EventAttendeeDTO eventAttendeeDTO){
        Optional<User> dbUser = userRepository.findById(eventAttendeeDTO.getUserId());
        if(dbUser.isPresent()){
            User user = dbUser.get();
            String lastName = (user.getLastName() != null ? " " + user.getLastName(): "");
            String userName = user.getFirstName() + lastName;
            eventAttendeeDTO.setUserName(userName);
            //TODO: set contact number as user phone number after adding it in database
            eventAttendeeDTO.setContactNumber("000");
        }
        else{//if user is not found, set the name to empty string instead of null
            eventAttendeeDTO.setUserName("");
            eventAttendeeDTO.setContactNumber("");
        }
        Optional<UserUniInfo> dbUserUniInfo = userUniInfoRepository.findOneByUserId(eventAttendeeDTO.getUserId());
        if(dbUserUniInfo.isPresent()){
            UserUniInfo userUniInfo = dbUserUniInfo.get();
            eventAttendeeDTO.setYearSession(userUniInfo.getYearSession());
        }
        else{//if userUniInfo is not found, set the yearSession to empty string instead of null
            eventAttendeeDTO.setYearSession("");
        }
        return eventAttendeeDTO;
    }
}