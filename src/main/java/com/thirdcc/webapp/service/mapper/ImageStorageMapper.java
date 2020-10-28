package com.thirdcc.webapp.service.mapper;

import com.thirdcc.webapp.domain.*;
import com.thirdcc.webapp.service.dto.ImageStorageDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link ImageStorage} and its DTO {@link ImageStorageDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ImageStorageMapper extends EntityMapper<ImageStorageDTO, ImageStorage> {



    default ImageStorage fromId(Long id) {
        if (id == null) {
            return null;
        }
        ImageStorage imageStorage = new ImageStorage();
        imageStorage.setId(id);
        return imageStorage;
    }
}
