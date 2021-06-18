package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.Event;
import com.thirdcc.webapp.service.dto.EventDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.Event}.
 */
public interface EventService {

    /**
     * Save a event.
     *
     * @param eventDTO the entity to save.
     * @param multipartFile Event Image File
     * @return the persisted entity.
     */
    EventDTO save(EventDTO eventDTO, MultipartFile multipartFile);

    /**
     * Update a event.
     *
     * @param eventDTO the entity to update.
     * @param multipartFile
     * @return the persisted entity.
     */
    EventDTO update(EventDTO eventDTO, MultipartFile multipartFile);

    /**
     * Get all the events.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EventDTO> findAll(Pageable pageable);

    /**
     * Get all the events and filter by date.
     *
     * @param pageable the pagination information.
     * @param fromDate the starting of start_date to query the entity.
     * @param toDate the ending of start_date to query the entity.
     * @return the list of entities.
     */
    Page<EventDTO> findAllByDateRange(Pageable pageable, String fromDate, String toDate);

    /**
     * Get all the upcoming events.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EventDTO> findAllUpcomingEvents(Pageable pageable);

    /**
     * Get all the past events.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EventDTO> findAllPastEvents(Pageable pageable);

    /**
     * Get the "id" event.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EventDTO> findOne(Long id);

    /**
     * Delete the "id" event.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Find the "id" event with not cancelled status.
     *
     * @param eventId the id of the entity.
     */
    Event findEventByIdAndNotCancelledStatus(Long eventId);

    /**
     * Cancel the "id" event.
     *
     * @param eventId the id of the entity.
     */
    EventDTO cancelEventById(Long eventId);

}
