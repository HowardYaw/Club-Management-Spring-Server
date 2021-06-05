package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.UserUniInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the UserUniInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserUniInfoRepository extends JpaRepository<UserUniInfo, Long>, JpaSpecificationExecutor<UserUniInfo> {

    Optional<UserUniInfo> findOneByUserId(Long userId);
}
