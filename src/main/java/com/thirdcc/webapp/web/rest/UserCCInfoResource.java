package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.service.UserCCInfoService;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
import com.thirdcc.webapp.service.dto.UserCCInfoDTO;

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
 * REST controller for managing {@link com.thirdcc.webapp.domain.UserCCInfo}.
 */
@RestController
@RequestMapping("/api")
public class UserCCInfoResource {

    private final Logger log = LoggerFactory.getLogger(UserCCInfoResource.class);

    private static final String ENTITY_NAME = "userCCInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserCCInfoService userCCInfoService;

    public UserCCInfoResource(UserCCInfoService userCCInfoService) {
        this.userCCInfoService = userCCInfoService;
    }

    /**
     * {@code POST  /user-cc-infos} : Create a new userCCInfo.
     *
     * @param userCCInfoDTO the userCCInfoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userCCInfoDTO, or with status {@code 400 (Bad Request)} if the userCCInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-cc-infos")
    public ResponseEntity<UserCCInfoDTO> createUserCCInfo(@RequestBody UserCCInfoDTO userCCInfoDTO) throws URISyntaxException {
        log.debug("REST request to save UserCCInfo : {}", userCCInfoDTO);
        if (userCCInfoDTO.getId() != null) {
            throw new BadRequestAlertException("A new userCCInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserCCInfoDTO result = userCCInfoService.save(userCCInfoDTO);
        return ResponseEntity.created(new URI("/api/user-cc-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-cc-infos} : Updates an existing userCCInfo.
     *
     * @param userCCInfoDTO the userCCInfoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userCCInfoDTO,
     * or with status {@code 400 (Bad Request)} if the userCCInfoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userCCInfoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-cc-infos")
    public ResponseEntity<UserCCInfoDTO> updateUserCCInfo(@RequestBody UserCCInfoDTO userCCInfoDTO) throws URISyntaxException {
        log.debug("REST request to update UserCCInfo : {}", userCCInfoDTO);
        if (userCCInfoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UserCCInfoDTO result = userCCInfoService.save(userCCInfoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userCCInfoDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /user-cc-infos} : get all the userCCInfos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userCCInfos in body.
     */
    @GetMapping("/user-cc-infos")
    public List<UserCCInfoDTO> getAllUserCCInfos() {
        log.debug("REST request to get all UserCCInfos");
        return userCCInfoService.findAll();
    }

    /**
     * {@code GET  /user-cc-infos/:id} : get the "id" userCCInfo.
     *
     * @param id the id of the userCCInfoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userCCInfoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-cc-infos/{id}")
    public ResponseEntity<UserCCInfoDTO> getUserCCInfo(@PathVariable Long id) {
        log.debug("REST request to get UserCCInfo : {}", id);
        Optional<UserCCInfoDTO> userCCInfoDTO = userCCInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userCCInfoDTO);
    }

    /**
     * {@code DELETE  /user-cc-infos/:id} : delete the "id" userCCInfo.
     *
     * @param id the id of the userCCInfoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-cc-infos/{id}")
    public ResponseEntity<Void> deleteUserCCInfo(@PathVariable Long id) {
        log.debug("REST request to delete UserCCInfo : {}", id);
        userCCInfoService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
