package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.Administrator;
import com.thirdcc.webapp.domain.YearSession;
import com.thirdcc.webapp.domain.enumeration.AdministratorRole;
import com.thirdcc.webapp.domain.enumeration.AdministratorStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Administrator entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {

    Optional<Administrator> findByUserIdAndYearSessionAndRoleAndStatus(Long userId, String yearSession, AdministratorRole administratorRole, AdministratorStatus status);

    Optional<Administrator> findByUserIdAndYearSessionAndStatus(Long id, String currentYearSession, AdministratorStatus status);
}
