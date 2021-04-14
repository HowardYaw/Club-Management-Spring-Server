package com.thirdcc.webapp.lib.wrapper;

import com.thirdcc.webapp.service.dto.ImageStorageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudStorage {

    /**
     * Save file and produce an url
     *
     * @param multipartFile
     * @return url to the file
     * @throws IOException
     */
    String store(MultipartFile multipartFile) throws IOException;
}
