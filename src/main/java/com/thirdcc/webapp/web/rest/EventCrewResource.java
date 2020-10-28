package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.service.EventCrewService;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
import com.thirdcc.webapp.service.dto.EventCrewDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public EventCrewResource(EventCrewService eventCrewService) {
        this.eventCrewService = eventCrewService;
    }

    /**
     * {@code POST  /event-crews} : Create a new eventCrew.
     *
     * @param eventCrewDTO the eventCrewDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventCrewDTO, or with status {@code 400 (Bad Request)} if the eventCrew has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/event-crews")
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

    /**
     * {@code GET  /event-crews} : get all the eventCrews.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eventCrews in body.
     */
    @GetMapping("/event-crews")
    public List<EventCrewDTO> getAllEventCrews() {
        log.debug("REST request to get all EventCrews");
        return eventCrewService.findAll();
    }

    /**
     * {@code GET  /event-crews/:id} : get the "id" eventCrew.
     *
     * @param id the id of the eventCrewDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventCrewDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/event-crews/{id}")
    public ResponseEntity<EventCrewDTO> getEventCrew(@PathVariable Long id) {
        log.debug("REST request to get EventCrew : {}", id);
        Optional<EventCrewDTO> eventCrewDTO = eventCrewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventCrewDTO);
    }

    /**
     * {@code DELETE  /event-crews/:id} : delete the "id" eventCrew.
     *
     * @param id the id of the eventCrewDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/event-crews/{id}")
    public ResponseEntity<Void> deleteEventCrew(@PathVariable Long id) {
        log.debug("REST request to delete EventCrew : {}", id);
        eventCrewService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
