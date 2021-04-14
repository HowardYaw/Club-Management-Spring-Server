package com.thirdcc.webapp.lib.wrapper;

import com.google.cloud.storage.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
public class GCPCloudStorage implements CloudStorage {
    private Storage storage = StorageOptions.newBuilder().setProjectId("ccclubmanagement").build().getService();

    @Override
    public String store(MultipartFile multipartFile) throws IOException {
        BlobId blobId = BlobId.of("cc-club-management-bucket-1", Objects.requireNonNull(multipartFile.getOriginalFilename()));
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        // Create object in bucket
        storage.create(blobInfo, multipartFile.getInputStream());

        // Set ACL to public
        storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        return storage.get(blobId).getMediaLink();
    }
}
