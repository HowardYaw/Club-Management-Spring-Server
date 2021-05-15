package com.thirdcc.webapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserCCInfoDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(UserCCInfoDTO.class);
    UserCCInfoDTO userCCInfoDTO1 = new UserCCInfoDTO();
    userCCInfoDTO1.setId(1L);
    UserCCInfoDTO userCCInfoDTO2 = new UserCCInfoDTO();
    assertThat(userCCInfoDTO1).isNotEqualTo(userCCInfoDTO2);
    userCCInfoDTO2.setId(userCCInfoDTO1.getId());
    assertThat(userCCInfoDTO1).isEqualTo(userCCInfoDTO2);
    userCCInfoDTO2.setId(2L);
    assertThat(userCCInfoDTO1).isNotEqualTo(userCCInfoDTO2);
    userCCInfoDTO1.setId(null);
    assertThat(userCCInfoDTO1).isNotEqualTo(userCCInfoDTO2);
  }
}
