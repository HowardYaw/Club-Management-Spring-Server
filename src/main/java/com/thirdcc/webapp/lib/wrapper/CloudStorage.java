package com.thirdcc.webapp.lib.wrapper;

import com.thirdcc.webapp.service.dto.ImageStorageDTO;
import org.springframework.web.multipart.MultipartFile;

public interface CloudStorage {
    ImageStorageDTO store(MultipartFile multipartFile);
    Byte[] download(ImageStorageDTO imageStorageDTO);
}
