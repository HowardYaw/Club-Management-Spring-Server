package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.service.EventImageService;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
import com.thirdcc.webapp.service.dto.EventImageDTO;

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
 * REST controller for managing {@link com.thirdcc.webapp.domain.EventImage}.
 */
@RestController
@RequestMapping("/api")
public class EventImageResource {

    private final Logger log = LoggerFactory.getLogger(EventImageResource.class);

    private static final String ENTITY_NAME = "eventImage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventImageService eventImageService;

    public EventImageResource(EventImageService eventImageService) {
        this.eventImageService = eventImageService;
    }

    /**
     * {@code POST  /event-images} : Create a new eventImage.
     *
     * @param eventImageDTO the eventImageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventImageDTO, or with status {@code 400 (Bad Request)} if the eventImage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/event-images")
    public ResponseEntity<EventImageDTO> createEventImage(@RequestBody EventImageDTO eventImageDTO) throws URISyntaxException {
        log.debug("REST request to save EventImage : {}", eventImageDTO);
        if (eventImageDTO.getId() != null) {
            throw new BadRequestAlertException("A new eventImage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EventImageDTO result = eventImageService.save(eventImageDTO);
        return ResponseEntity.created(new URI("/api/event-images/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /event-images} : Updates an existing eventImage.
     *
     * @param eventImageDTO the eventImageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventImageDTO,
     * or with status {@code 400 (Bad Request)} if the eventImageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventImageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/event-images")
    public ResponseEntity<EventImageDTO> updateEventImage(@RequestBody EventImageDTO eventImageDTO) throws URISyntaxException {
        log.debug("REST request to update EventImage : {}", eventImageDTO);
        if (eventImageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EventImageDTO result = eventImageService.save(eventImageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eventImageDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /event-images} : get all the eventImages.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eventImages in body.
     */
    @GetMapping("/event-images")
    public List<EventImageDTO> getAllEventImages() {
        log.debug("REST request to get all EventImages");
        return eventImageService.findAll();
    }

    /**
     * {@code GET  /event-images/:id} : get the "id" eventImage.
     *
     * @param id the id of the eventImageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventImageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/event-images/{id}")
    public ResponseEntity<EventImageDTO> getEventImage(@PathVariable Long id) {
        log.debug("REST request to get EventImage : {}", id);
        Optional<EventImageDTO> eventImageDTO = eventImageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventImageDTO);
    }

    /**
     * {@code DELETE  /event-images/:id} : delete the "id" eventImage.
     *
     * @param id the id of the eventImageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/event-images/{id}")
    public ResponseEntity<Void> deleteEventImage(@PathVariable Long id) {
        log.debug("REST request to delete EventImage : {}", id);
        eventImageService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
