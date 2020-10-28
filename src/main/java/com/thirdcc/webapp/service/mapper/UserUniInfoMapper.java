package com.thirdcc.webapp.service.mapper;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.service.dto.UserUniInfoDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserUniInfo} and its DTO {@link UserUniInfoDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface UserUniInfoMapper extends EntityMapper<UserUniInfoDTO, UserUniInfo> {



    default UserUniInfo fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserUniInfo userUniInfo = new UserUniInfo();
        userUniInfo.setId(id);
        return userUniInfo;
    }
}
