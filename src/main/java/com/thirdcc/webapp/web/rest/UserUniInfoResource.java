package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.domain.User;
import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.security.SecurityUtils;
import com.thirdcc.webapp.service.UserService;
import com.thirdcc.webapp.service.UserUniInfoService;
import com.thirdcc.webapp.web.rest.errors.BadRequestAlertException;
import com.thirdcc.webapp.service.dto.UserUniInfoDTO;

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
 * REST controller for managing {@link com.thirdcc.webapp.domain.UserUniInfo}.
 */
@RestController
@RequestMapping("/api")
public class UserUniInfoResource {

    private final Logger log = LoggerFactory.getLogger(UserUniInfoResource.class);

    private static final String ENTITY_NAME = "userUniInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserUniInfoService userUniInfoService;

    private final UserService userService;

    public UserUniInfoResource(UserUniInfoService userUniInfoService,
                               UserService userService
    ) {
        this.userUniInfoService = userUniInfoService;
        this.userService = userService;
    }

    /**
     * {@code POST  /user-uni-infos} : Create a new userUniInfo.
     *
     * @param userUniInfoDTO the userUniInfoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userUniInfoDTO, or with status {@code 400 (Bad Request)} if the userUniInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-uni-infos")
    public ResponseEntity<UserUniInfoDTO> createUserUniInfo(@RequestBody UserUniInfoDTO userUniInfoDTO) throws URISyntaxException {
        log.debug("REST request to save UserUniInfo : {}", userUniInfoDTO);
        if (userUniInfoDTO.getId() != null) {
            throw new BadRequestAlertException("A new userUniInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserUniInfoDTO result = userUniInfoService.save(userUniInfoDTO);
        return ResponseEntity.created(new URI("/api/user-uni-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-uni-infos} : Updates an existing userUniInfo.
     *
     * @param userUniInfoDTO the userUniInfoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userUniInfoDTO,
     * or with status {@code 400 (Bad Request)} if the userUniInfoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userUniInfoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-uni-infos")
    public ResponseEntity<UserUniInfoDTO> updateUserUniInfo(@RequestBody UserUniInfoDTO userUniInfoDTO) throws URISyntaxException {
        log.debug("REST request to update UserUniInfo : {}", userUniInfoDTO);
        if (userUniInfoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UserUniInfoDTO result = userUniInfoService.save(userUniInfoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userUniInfoDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /user-uni-infos} : get all the userUniInfos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userUniInfos in body.
     */
    @GetMapping("/user-uni-infos")
    public List<UserUniInfoDTO> getAllUserUniInfos() {
        log.debug("REST request to get all UserUniInfos");
        return userUniInfoService.findAll();
    }

    /**
     * {@code GET  /user-uni-infos/:id} : get the "id" userUniInfo.
     *
     * @param id the id of the userUniInfoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userUniInfoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-uni-infos/{id}")
    public ResponseEntity<UserUniInfoDTO> getUserUniInfo(@PathVariable Long id) {
        log.debug("REST request to get UserUniInfo : {}", id);
        Optional<UserUniInfoDTO> userUniInfoDTO = userUniInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userUniInfoDTO);
    }

    /**
     * {@code GET /users/current} : get current login user details
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-uni-infos/current")
    public ResponseEntity<UserUniInfoDTO> getCurrentUserDetailsWithUniInfo() {
        log.debug("REST request to get Current User Details with Uni Info");
        String userLogin = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestException("User not Login"));
        User user = userService.getUserByLogin(userLogin)
            .orElseThrow(() -> new BadRequestException("User not found"));
        Optional<UserUniInfoDTO> userUniInfoDTO = userUniInfoService.getUserUniInfoByUserId(user.getId());
        userUniInfoDTO = userUniInfoService.mapUserUniInfoWithUser(userUniInfoDTO.orElse(new UserUniInfoDTO()), user);
        return ResponseUtil.wrapOrNotFound(userUniInfoDTO);
    }

    /**
     * {@code DELETE  /user-uni-infos/:id} : delete the "id" userUniInfo.
     *
     * @param id the id of the userUniInfoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-uni-infos/{id}")
    public ResponseEntity<Void> deleteUserUniInfo(@PathVariable Long id) {
        log.debug("REST request to delete UserUniInfo : {}", id);
        userUniInfoService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
