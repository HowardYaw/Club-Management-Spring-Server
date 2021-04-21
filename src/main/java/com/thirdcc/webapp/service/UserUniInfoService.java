package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.User;
import com.thirdcc.webapp.service.dto.UserUniInfoDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.UserUniInfo}.
 */
public interface UserUniInfoService {

    /**
     * Save a userUniInfo.
     *
     * @param userUniInfoDTO the entity to save.
     * @return the persisted entity.
     */
    UserUniInfoDTO save(UserUniInfoDTO userUniInfoDTO);

    /**
     * Get all the userUniInfos.
     *
     * @return the list of entities.
     */
    List<UserUniInfoDTO> findAll();


    /**
     * Get the "id" userUniInfo.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserUniInfoDTO> findOne(Long id);

    /**
     * Delete the "id" userUniInfo.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    boolean isUserUniInfoCompleted(Long userId);

    Optional<UserUniInfoDTO> getUserUniInfoByUserId(Long id);

    Optional<UserUniInfoDTO> mapUserUniInfoWithUser(UserUniInfoDTO userUniInfoDTO, User user);
}
