package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.service.EventAttendeeQueryService;
import com.thirdcc.webapp.service.EventAttendeeService;
import com.thirdcc.webapp.service.criteria.EventAttendeeCriteria;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
import com.thirdcc.webapp.service.dto.EventAttendeeDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.thirdcc.webapp.domain.EventAttendee}.
 */
@RestController
@RequestMapping("/api")
public class EventAttendeeResource {

    private final Logger log = LoggerFactory.getLogger(EventAttendeeResource.class);

    private static final String ENTITY_NAME = "eventAttendee";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventAttendeeService eventAttendeeService;

    private final EventAttendeeQueryService eventAttendeeQueryService;

    public EventAttendeeResource(EventAttendeeService eventAttendeeService, EventAttendeeQueryService eventAttendeeQueryService) {
        this.eventAttendeeService = eventAttendeeService;
        this.eventAttendeeQueryService = eventAttendeeQueryService;
    }

    /**
     * {@code POST  /event-attendees} : Create a new eventAttendee.
     *
     * @param eventAttendeeDTO the eventAttendeeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventAttendeeDTO, or with status {@code 400 (Bad Request)} if the eventAttendee has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/event-attendees")
    public ResponseEntity<EventAttendeeDTO> createEventAttendee(@RequestBody EventAttendeeDTO eventAttendeeDTO) throws URISyntaxException {
        log.debug("REST request to save EventAttendee : {}", eventAttendeeDTO);
        if (eventAttendeeDTO.getId() != null) {
            throw new BadRequestAlertException("A new eventAttendee cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EventAttendeeDTO result = eventAttendeeService.save(eventAttendeeDTO);
        return ResponseEntity.created(new URI("/api/event-attendees/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /event-attendees} : Updates an existing eventAttendee.
     *
     * @param eventAttendeeDTO the eventAttendeeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventAttendeeDTO,
     * or with status {@code 400 (Bad Request)} if the eventAttendeeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventAttendeeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/event-attendees")
    public ResponseEntity<EventAttendeeDTO> updateEventAttendee(@RequestBody EventAttendeeDTO eventAttendeeDTO) throws URISyntaxException {
        log.debug("REST request to update EventAttendee : {}", eventAttendeeDTO);
        if (eventAttendeeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EventAttendeeDTO result = eventAttendeeService.save(eventAttendeeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eventAttendeeDTO.getId().toString()))
            .body(result);
    }

    @GetMapping("/event-attendees")
    public ResponseEntity<List<EventAttendeeDTO>> getAllEventAttendees(EventAttendeeCriteria criteria, Pageable pageable) {
        log.debug("REST request to get EventAttendees by criteria: {}", criteria);
        Page<EventAttendeeDTO> page = eventAttendeeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/event-attendees/count")
    public ResponseEntity<Long> countEventAttendees(EventAttendeeCriteria criteria) {
        log.debug("REST request to count EventAttendees by criteria: {}", criteria);
        return ResponseEntity.ok().body(eventAttendeeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /event-attendees/:id} : get the "id" eventAttendee.
     *
     * @param id the id of the eventAttendeeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventAttendeeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/event-attendees/{id}")
    public ResponseEntity<EventAttendeeDTO> getEventAttendee(@PathVariable Long id) {
        log.debug("REST request to get EventAttendee : {}", id);
        Optional<EventAttendeeDTO> eventAttendeeDTO = eventAttendeeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventAttendeeDTO);
    }

    /**
     * {@code GET  /event-attendees/event/{eventId}} : GET the eventAttendee in "eventId" event.
     *
     * @param eventId the id of the event to retrieve list of eventAttendeeDTO.
     * @return the {@link ResponseEntity} with status {@code 200 (OK) or 400 (BadRequest)}.
     */
    @GetMapping("/event-attendees/event/{eventId}")
    @PreAuthorize("@managementTeamSecurityExpression.isEventCrew(#eventId) || @managementTeamSecurityExpression.isCurrentAdministrator()")
    public ResponseEntity<List<EventAttendeeDTO>> getAllEventAttendeeByEventId(Pageable pageable, @PathVariable Long eventId, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get a page of EventAttendees");
        Page<EventAttendeeDTO> page = eventAttendeeService.findAllByEventId(pageable, eventId);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code DELETE  /event-attendees/:id} : delete the "id" eventAttendee.
     *
     * @param id the id of the eventAttendeeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/event-attendees/{id}")
    public ResponseEntity<Void> deleteEventAttendee(@PathVariable Long id) {
        log.debug("REST request to delete EventAttendee : {}", id);
        eventAttendeeService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code GET  /event-attendees/event/{eventId}} : GET the eventAttendee in "eventId" event.
     *
     * @param eventId the id of the event to retrieve list of eventAttendeeDTO.
     * @param userId theid of the user to retrieve list of eventAttendeeDTO.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @GetMapping("/event-attendees/event/{eventId}/user/{userId}")
    public ResponseEntity<EventAttendeeDTO> getEventAttendeeByEventIdAndUserId(@PathVariable Long eventId, @PathVariable Long userId) {
        log.debug("REST request to get a EventAttendee with event Id and user Id");
        Optional<EventAttendeeDTO> eventAttendeeDTO = eventAttendeeService.findOneByEventIdAndUserId(eventId, userId);
        return ResponseEntity.ok().body(eventAttendeeDTO.orElse(new EventAttendeeDTO()));
    }
}
