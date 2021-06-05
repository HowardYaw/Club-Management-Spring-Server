package com.thirdcc.webapp.repository;

import com.thirdcc.webapp.domain.ImageStorage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ImageStorage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ImageStorageRepository extends JpaRepository<ImageStorage, Long>, JpaSpecificationExecutor<ImageStorage> {}
