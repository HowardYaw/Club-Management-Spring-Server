package com.thirdcc.webapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserUniInfoDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(UserUniInfoDTO.class);
    UserUniInfoDTO userUniInfoDTO1 = new UserUniInfoDTO();
    userUniInfoDTO1.setId(1L);
    UserUniInfoDTO userUniInfoDTO2 = new UserUniInfoDTO();
    assertThat(userUniInfoDTO1).isNotEqualTo(userUniInfoDTO2);
    userUniInfoDTO2.setId(userUniInfoDTO1.getId());
    assertThat(userUniInfoDTO1).isEqualTo(userUniInfoDTO2);
    userUniInfoDTO2.setId(2L);
    assertThat(userUniInfoDTO1).isNotEqualTo(userUniInfoDTO2);
    userUniInfoDTO1.setId(null);
    assertThat(userUniInfoDTO1).isNotEqualTo(userUniInfoDTO2);
  }
}
