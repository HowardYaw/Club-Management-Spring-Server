package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.ClubFamily;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ClubFamily entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClubFamilyRepository extends JpaRepository<ClubFamily, Long> {

}
