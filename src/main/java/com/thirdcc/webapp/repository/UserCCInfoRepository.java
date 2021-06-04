package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.UserCCInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the UserCCInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserCCInfoRepository extends JpaRepository<UserCCInfo, Long>, JpaSpecificationExecutor<UserCCInfo> {

    Page<UserCCInfo> findAllByUserId(Long userId, Pageable pageable);

    Optional<UserCCInfo> findByUserId(Long userId);
}
