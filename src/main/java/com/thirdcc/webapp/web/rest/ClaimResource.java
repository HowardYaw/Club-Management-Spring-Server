package com.thirdcc.webapp.web.rest;

import com.thirdcc.webapp.domain.enumeration.ClaimStatus;
import com.thirdcc.webapp.service.ClaimService;
import com.thirdcc.webapp.service.dto.ClaimDTO;

import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * REST controller for managing {@link com.thirdcc.webapp.domain.Claim}.
 */
@RestController
@RequestMapping("/api")
public class ClaimResource {

    private final Logger log = LoggerFactory.getLogger(ClaimResource.class);

    private static final String ENTITY_NAME = "claim";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClaimService claimService;

    public ClaimResource(ClaimService claimService) {
        this.claimService = claimService;
    }

    /**
     * {@code PUT  /claims} : Updates an existing claim status.
     *
     * @param id the id of the claim to update
     * @param claimStatus the claimStatus to update to
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated claimDTO,
     * or with status {@code 400 (Bad Request)} if the claimDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the claimDTO couldn't be updated.
     */
    @PutMapping("/claims/{id}/status/{claimStatus}")
    @PreAuthorize("@managementTeamSecurityExpression.isCurrentAdministrator()")
    public ResponseEntity<ClaimDTO> updateClaimStatus(@PathVariable Long id, @PathVariable ClaimStatus claimStatus) {
        log.debug("REST request to update claim: {} with status: {}", id, claimStatus);
        ClaimDTO result = claimService.updateStatus(id, claimStatus);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }

    /**
     * {@code GET  /claims} : get all the claims with Open ClaimStatus.
     *
     * @param pageable the pagination information.
     * @param queryParams a {@link MultiValueMap} query parameters.
     * @param uriBuilder a {@link UriComponentsBuilder} URI builder.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of claims in body.
     */
    @GetMapping("/claims")
    @PreAuthorize("@managementTeamSecurityExpression.isCurrentAdministrator()")
    public ResponseEntity<List<ClaimDTO>> getAllOpenClaims(Pageable pageable, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get a page of Claims");
        Page<ClaimDTO> page = claimService.findAllOpenClaims(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
