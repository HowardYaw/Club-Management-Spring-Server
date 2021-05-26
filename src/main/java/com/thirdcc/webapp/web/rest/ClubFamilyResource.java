package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.service.ClubFamilyQueryService;
import com.thirdcc.webapp.service.ClubFamilyService;
import com.thirdcc.webapp.service.criteria.ClubFamilyCriteria;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
import com.thirdcc.webapp.service.dto.ClubFamilyDTO;

import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
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
 * REST controller for managing {@link com.thirdcc.webapp.domain.ClubFamily}.
 */
@RestController
@RequestMapping("/api")
public class ClubFamilyResource {

    private final Logger log = LoggerFactory.getLogger(ClubFamilyResource.class);

    private static final String ENTITY_NAME = "clubFamily";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClubFamilyService clubFamilyService;

    private final ClubFamilyQueryService clubFamilyQueryService;

    public ClubFamilyResource(ClubFamilyService clubFamilyService, ClubFamilyQueryService clubFamilyQueryService) {
        this.clubFamilyService = clubFamilyService;
        this.clubFamilyQueryService = clubFamilyQueryService;
    }

    /**
     * {@code POST  /club-families} : Create a new clubFamily.
     *
     * @param clubFamilyDTO the clubFamilyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clubFamilyDTO, or with status {@code 400 (Bad Request)} if the clubFamily has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/club-families")
    public ResponseEntity<ClubFamilyDTO> createClubFamily(@RequestBody ClubFamilyDTO clubFamilyDTO) throws URISyntaxException {
        log.debug("REST request to save ClubFamily : {}", clubFamilyDTO);
        if (clubFamilyDTO.getId() != null) {
            throw new BadRequestAlertException("A new clubFamily cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ClubFamilyDTO result = clubFamilyService.save(clubFamilyDTO);
        return ResponseEntity.created(new URI("/api/club-families/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /club-families} : Updates an existing clubFamily.
     *
     * @param clubFamilyDTO the clubFamilyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clubFamilyDTO,
     * or with status {@code 400 (Bad Request)} if the clubFamilyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clubFamilyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/club-families")
    public ResponseEntity<ClubFamilyDTO> updateClubFamily(@RequestBody ClubFamilyDTO clubFamilyDTO) throws URISyntaxException {
        log.debug("REST request to update ClubFamily : {}", clubFamilyDTO);
        if (clubFamilyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ClubFamilyDTO result = clubFamilyService.save(clubFamilyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clubFamilyDTO.getId().toString()))
            .body(result);
    }

    @GetMapping("/club-families")
    public ResponseEntity<List<ClubFamilyDTO>> getAllClubFamilies(ClubFamilyCriteria criteria) {
        log.debug("REST request to get ClubFamilies by criteria: {}", criteria);
        List<ClubFamilyDTO> entityList = clubFamilyQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    @GetMapping("/club-families/count")
    public ResponseEntity<Long> countClubFamilies(ClubFamilyCriteria criteria) {
        log.debug("REST request to count ClubFamilies by criteria: {}", criteria);
        return ResponseEntity.ok().body(clubFamilyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /club-families/:id} : get the "id" clubFamily.
     *
     * @param id the id of the clubFamilyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clubFamilyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/club-families/{id}")
    public ResponseEntity<ClubFamilyDTO> getClubFamily(@PathVariable Long id) {
        log.debug("REST request to get ClubFamily : {}", id);
        Optional<ClubFamilyDTO> clubFamilyDTO = clubFamilyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clubFamilyDTO);
    }

    /**
     * {@code DELETE  /club-families/:id} : delete the "id" clubFamily.
     *
     * @param id the id of the clubFamilyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/club-families/{id}")
    public ResponseEntity<Void> deleteClubFamily(@PathVariable Long id) {
        log.debug("REST request to delete ClubFamily : {}", id);
        clubFamilyService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
