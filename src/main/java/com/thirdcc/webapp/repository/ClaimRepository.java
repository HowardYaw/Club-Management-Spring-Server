package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.Claim;
import com.thirdcc.webapp.domain.enumeration.ClaimStatus;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Claim entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
        Page<Claim> findAllByStatusIn(Pageable pageable, Set<ClaimStatus> claimStatus);
}
