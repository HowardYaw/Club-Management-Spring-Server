package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.domain.enumeration.AdministratorStatus;
import com.thirdcc.webapp.service.AdministratorQueryService;
import com.thirdcc.webapp.service.AdministratorService;
import com.thirdcc.webapp.service.criteria.AdministratorCriteria;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
import com.thirdcc.webapp.service.dto.AdministratorDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link com.thirdcc.webapp.domain.Administrator}.
 */
@RestController
@RequestMapping("/api")
public class AdministratorResource {

    private final Logger log = LoggerFactory.getLogger(AdministratorResource.class);

    private static final String ENTITY_NAME = "administrator";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AdministratorService administratorService;
    private final AdministratorQueryService administratorQueryService;

    public AdministratorResource(AdministratorService administratorService, AdministratorQueryService administratorQueryService) {
        this.administratorService = administratorService;
        this.administratorQueryService = administratorQueryService;
    }

    /**
     * {@code POST  /administrators} : Create a new administrator.
     *
     * @param administratorDTO the administratorDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new administratorDTO, or with status {@code 400 (Bad Request)} if the administrator has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/administrators")
    @PreAuthorize("@managementTeamSecurityExpression.isCurrentAdministrator()")
    public ResponseEntity<AdministratorDTO> createAdministrator(@RequestBody AdministratorDTO administratorDTO) throws URISyntaxException {
        log.debug("REST request to save Administrator : {}", administratorDTO);
        if (administratorDTO.getId() != null) {
            throw new BadRequestAlertException("A new administrator cannot already have an ID", ENTITY_NAME, "idexists");
        }
        administratorDTO.setStatus(AdministratorStatus.ACTIVE);
        AdministratorDTO result = administratorService.save(administratorDTO);
        return ResponseEntity.created(new URI("/api/administrators/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /administrators} : Updates an existing administrator.
     *
     * @param administratorDTO the administratorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated administratorDTO,
     * or with status {@code 400 (Bad Request)} if the administratorDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the administratorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/administrators")
    @PreAuthorize("@managementTeamSecurityExpression.isCurrentAdministrator()")
    public ResponseEntity<AdministratorDTO> updateAdministrator(@RequestBody AdministratorDTO administratorDTO) throws URISyntaxException {
        log.debug("REST request to update Administrator : {}", administratorDTO);
        if (administratorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        AdministratorDTO result = administratorService.save(administratorDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, administratorDTO.getId().toString()))
            .body(result);
    }

    @GetMapping("/administrators")
    public ResponseEntity<List<AdministratorDTO>> getAllAdministrators(AdministratorCriteria criteria) {
        log.debug("REST request to get Administrators by criteria: {}", criteria);
        List<AdministratorDTO> entityList = administratorQueryService.findByCriteria(criteria)
            .stream()
            .map(administratorService::mapUserDetails)
            .collect(Collectors.toList());
        return ResponseEntity.ok().body(entityList);
    }

    @GetMapping("/administrators/count")
    public ResponseEntity<Long> countAdministrators(AdministratorCriteria criteria) {
        log.debug("REST request to count Administrators by criteria: {}", criteria);
        return ResponseEntity.ok().body(administratorQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /administrators/:id} : get the "id" administrator.
     *
     * @param id the id of the administratorDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the administratorDTO, or with status {@code 404 (Not Found)}.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/administrators/{id}")
    public ResponseEntity<AdministratorDTO> getAdministrator(@PathVariable Long id) {
        log.debug("REST request to get Administrator : {}", id);
        Optional<AdministratorDTO> administratorDTO = administratorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(administratorDTO);
    }

    /**
     * {@code DELETE  /administrators/:id} : delete the "id" administrator.
     *
     * @param id the id of the administratorDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/administrators/{id}")
    @PreAuthorize("@managementTeamSecurityExpression.isCurrentAdministrator()")
    public ResponseEntity<Void> deleteAdministrator(@PathVariable Long id) {
        log.debug("REST request to delete Administrator : {}", id);
        administratorService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
