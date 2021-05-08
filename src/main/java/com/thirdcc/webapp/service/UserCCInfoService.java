package com.thirdcc.webapp.service;

import com.thirdcc.webapp.service.dto.UserCCInfoDTO;
import com.thirdcc.webapp.service.dto.UserCCRoleDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.thirdcc.webapp.domain.UserCCInfo}.
 */
public interface UserCCInfoService {

    /**
     * Save a userCCInfo.
     *
     * @param userCCInfoDTO the entity to save.
     * @return the persisted entity.
     */
    UserCCInfoDTO save(UserCCInfoDTO userCCInfoDTO);

    /**
     * Get all the userCCInfos.
     *
     * @return the list of entities.
     */
    List<UserCCInfoDTO> findAll();


    /**
     * Get the "id" userCCInfo.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserCCInfoDTO> findOne(Long id);

    /**
     * Get the "userId" userCCInfo.
     *
     * @param userId the userId of the entity.
     * @return the entity.
     */
    Optional<UserCCInfoDTO> findOneByUserId(Long userId);

    /**
     * Delete the "id" userCCInfo.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Get the List of UserCCInfo for User
     *
     * @param userId the id of User
     * @return List of UserCCInfo of User
     */
    List<UserCCInfoDTO> getUserCCInfoByUserId(Long userId);

    /**
     * Get the "userId" CC Roles.
     *
     * @param userId the userId of the User.
     * @return List of userCCRoles info.
     */
    List<UserCCRoleDTO> getUserCCRolesByUserId(Long userId);
}
