package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.EventImage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the EventImage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventImageRepository extends JpaRepository<EventImage, Long>, JpaSpecificationExecutor<EventImage> {}
