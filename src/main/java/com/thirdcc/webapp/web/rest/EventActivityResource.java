package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.service.EventActivityService;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
import com.thirdcc.webapp.service.dto.EventActivityDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.thirdcc.webapp.domain.EventActivity}.
 */
@RestController
@RequestMapping("/api")
public class EventActivityResource {

    private final Logger log = LoggerFactory.getLogger(EventActivityResource.class);

    private static final String ENTITY_NAME = "eventActivity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventActivityService eventActivityService;

    public EventActivityResource(EventActivityService eventActivityService) {
        this.eventActivityService = eventActivityService;
    }

    /**
     * {@code POST  /event-activities} : Create a new eventActivity.
     *
     * @param eventActivityDTO the eventActivityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventActivityDTO, or with status {@code 400 (Bad Request)} if the eventActivity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/event-activities")
    public ResponseEntity<EventActivityDTO> createEventActivity(@RequestBody EventActivityDTO eventActivityDTO) throws URISyntaxException {
        log.debug("REST request to save EventActivity : {}", eventActivityDTO);
        if (eventActivityDTO.getId() != null) {
            throw new BadRequestAlertException("A new eventActivity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EventActivityDTO result = eventActivityService.save(eventActivityDTO);
        return ResponseEntity.created(new URI("/api/event-activities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /event-activities} : Updates an existing eventActivity.
     *
     * @param eventActivityDTO the eventActivityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventActivityDTO,
     * or with status {@code 400 (Bad Request)} if the eventActivityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventActivityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/event-activities")
    public ResponseEntity<EventActivityDTO> updateEventActivity(@RequestBody EventActivityDTO eventActivityDTO) throws URISyntaxException {
        log.debug("REST request to update EventActivity : {}", eventActivityDTO);
        if (eventActivityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EventActivityDTO result = eventActivityService.update(eventActivityDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eventActivityDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /event-activities} : get all the eventActivities.
     *
     * @param pageable the pagination information.
     * @param queryParams a {@link MultiValueMap} query parameters.
     * @param uriBuilder a {@link UriComponentsBuilder} URI builder.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eventActivities in body.
     */
    @GetMapping("/event-activities")
    public ResponseEntity<List<EventActivityDTO>> getAllEventActivities(Pageable pageable, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get a page of EventActivities");
        Page<EventActivityDTO> page = eventActivityService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/event-activities/event/{eventId}")
    public ResponseEntity<List<EventActivityDTO>> getAllEventActivitiesByEventId(Pageable pageable, @PathVariable Long eventId, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get a page of EventActivities");
        Page<EventActivityDTO> page = eventActivityService.findAllByEventId(pageable, eventId);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /event-activities/:id} : get the "id" eventActivity.
     *
     * @param id the id of the eventActivityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventActivityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/event-activities/{id}")
    public ResponseEntity<EventActivityDTO> getEventActivity(@PathVariable Long id) {
        log.debug("REST request to get EventActivity : {}", id);
        Optional<EventActivityDTO> eventActivityDTO = eventActivityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventActivityDTO);
    }

    /**
     * {@code DELETE  /event-activities/:id} : delete the "id" eventActivity.
     *
     * @param id the id of the eventActivityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/event-activities/{id}")
    public ResponseEntity<Void> deleteEventActivity(@PathVariable Long id) {
        log.debug("REST request to delete EventActivity : {}", id);
        eventActivityService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
