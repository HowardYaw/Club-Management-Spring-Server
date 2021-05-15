package com.thirdcc.webapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ImageStorageDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(ImageStorageDTO.class);
    ImageStorageDTO imageStorageDTO1 = new ImageStorageDTO();
    imageStorageDTO1.setId(1L);
    ImageStorageDTO imageStorageDTO2 = new ImageStorageDTO();
    assertThat(imageStorageDTO1).isNotEqualTo(imageStorageDTO2);
    imageStorageDTO2.setId(imageStorageDTO1.getId());
    assertThat(imageStorageDTO1).isEqualTo(imageStorageDTO2);
    imageStorageDTO2.setId(2L);
    assertThat(imageStorageDTO1).isNotEqualTo(imageStorageDTO2);
    imageStorageDTO1.setId(null);
    assertThat(imageStorageDTO1).isNotEqualTo(imageStorageDTO2);
  }
}
