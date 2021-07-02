package com.thirdcc.webapp.service.mapper;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.service.dto.UserCCInfoDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserCCInfo} and its DTO {@link UserCCInfoDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UserCCInfoMapper extends EntityMapper<UserCCInfoDTO, UserCCInfo> {

//    @Mapping(source = "user", target = "user")
    UserCCInfoDTO toDto(UserCCInfo userCcInfo);

    @Mapping(source = "userId", target = "user")
    UserCCInfo toEntity(UserCCInfoDTO userCcInfoDTO);


    default UserCCInfo fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserCCInfo userCCInfo = new UserCCInfo();
        userCCInfo.setId(id);
        return userCCInfo;
    }
}
