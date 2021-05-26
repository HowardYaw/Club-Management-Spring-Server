package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.service.EventCrewQueryService;
import com.thirdcc.webapp.service.EventCrewService;
import com.thirdcc.webapp.service.criteria.EventCrewCriteria;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
import com.thirdcc.webapp.service.dto.EventCrewDTO;

import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.thirdcc.webapp.domain.EventCrew}.
 */
@RestController
@RequestMapping("/api")
public class EventCrewResource {

    private final Logger log = LoggerFactory.getLogger(EventCrewResource.class);

    private static final String ENTITY_NAME = "eventCrew";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventCrewService eventCrewService;

    private final EventCrewQueryService eventCrewQueryService;

    public EventCrewResource(EventCrewService eventCrewService, EventCrewQueryService eventCrewQueryService) {
        this.eventCrewService = eventCrewService;
        this.eventCrewQueryService = eventCrewQueryService;
    }

    /**
     * {@code POST  /event-crews} : Create a new eventCrew.
     *
     * @param eventCrewDTO the eventCrewDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventCrewDTO, or with status {@code 400 (Bad Request)} if the eventCrew has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/event-crews")
    @PreAuthorize("@managementTeamSecurityExpression.isCurrentAdministrator() || @managementTeamSecurityExpression.isEventHead(#eventCrewDTO.getEventId())")
    public ResponseEntity<EventCrewDTO> createEventCrew(@RequestBody EventCrewDTO eventCrewDTO) throws URISyntaxException {
        log.debug("REST request to save EventCrew : {}", eventCrewDTO);
        if (eventCrewDTO.getId() != null) {
            throw new BadRequestAlertException("A new eventCrew cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EventCrewDTO result = eventCrewService.save(eventCrewDTO);
        return ResponseEntity.created(new URI("/api/event-crews/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /event-crews} : Updates an existing eventCrew.
     *
     * @param eventCrewDTO the eventCrewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventCrewDTO,
     * or with status {@code 400 (Bad Request)} if the eventCrewDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventCrewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/event-crews")
    @PreAuthorize("@managementTeamSecurityExpression.isCurrentAdministrator() || @managementTeamSecurityExpression.isEventHead(#eventCrewDTO.getEventId())")
    public ResponseEntity<EventCrewDTO> updateEventCrew(@RequestBody EventCrewDTO eventCrewDTO) throws URISyntaxException {
        log.debug("REST request to update EventCrew : {}", eventCrewDTO);
        if (eventCrewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EventCrewDTO result = eventCrewService.save(eventCrewDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eventCrewDTO.getId().toString()))
            .body(result);
    }

    @GetMapping("/event-crews")
    public ResponseEntity<List<EventCrewDTO>> getAllEventCrews(EventCrewCriteria criteria) {
        log.debug("REST request to get EventCrews by criteria: {}", criteria);
        List<EventCrewDTO> entityList = eventCrewQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /event-crews/count} : count all the eventCrews.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/event-crews/count")
    public ResponseEntity<Long> countEventCrews(EventCrewCriteria criteria) {
        log.debug("REST request to count EventCrews by criteria: {}", criteria);
        return ResponseEntity.ok().body(eventCrewQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /event-crews/:id} : get the "id" eventCrew.
     *
     * @param id the id of the eventCrewDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventCrewDTO, or with status {@code 404 (Not Found)}.
     * @usage Use for getting eventCrew data when update
     */
    @GetMapping("/event-crews/{id}")
    @PreAuthorize("@managementTeamSecurityExpression.isCurrentAdministrator() || @managementTeamSecurityExpression.isEventHead(#eventId)")
    public ResponseEntity<EventCrewDTO> getEventCrew(@PathVariable Long id, @RequestParam(value = "eventId") String eventId) {
        log.debug("REST request to get EventCrew : {}", id);
        Optional<EventCrewDTO> eventCrewDTO = eventCrewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventCrewDTO);
    }

    /**
     * {@code GET  /event-crews/event/:eventId} : get the eventCrew from "eventId" event.
     *
     * @param eventId the id of the event to retrieve eventCrewDTO.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventCrewDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/event-crews/event/{eventId}")
    @PreAuthorize("@managementTeamSecurityExpression.isCurrentAdministrator() || @managementTeamSecurityExpression.isEventCrew(#eventId)")
    public ResponseEntity<List<EventCrewDTO>> getEventCrewWithEventId(Pageable pageable, @PathVariable Long eventId, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get EventCrew with Event Id: {}", eventId);
        Page<EventCrewDTO> page = eventCrewService.findAllByEventId(pageable, eventId);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code DELETE  /event-crews/:id} : delete the "id" eventCrew.
     *
     * @param id the id of the eventCrewDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/event-crews/{id}")
    @PreAuthorize("@managementTeamSecurityExpression.isCurrentAdministrator() || @managementTeamSecurityExpression.isEventHead(#eventId)")
    public ResponseEntity<Void> deleteEventCrew(@PathVariable Long id, @RequestParam(value = "eventId") String eventId) {
        log.debug("REST request to delete EventCrew : {}", id);
        eventCrewService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
