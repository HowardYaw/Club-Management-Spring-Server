package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.UserCCInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.DoubleStream;


/**
 * Spring Data  repository for the UserCCInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserCCInfoRepository extends JpaRepository<UserCCInfo, Long> {

    Optional<UserCCInfo> findByUserId(Long userId);
}
