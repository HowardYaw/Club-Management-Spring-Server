package com.thirdcc.webapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserCCInfoTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(UserCCInfo.class);
    UserCCInfo userCCInfo1 = new UserCCInfo();
    userCCInfo1.setId(1L);
    UserCCInfo userCCInfo2 = new UserCCInfo();
    userCCInfo2.setId(userCCInfo1.getId());
    assertThat(userCCInfo1).isEqualTo(userCCInfo2);
    userCCInfo2.setId(2L);
    assertThat(userCCInfo1).isNotEqualTo(userCCInfo2);
    userCCInfo1.setId(null);
    assertThat(userCCInfo1).isNotEqualTo(userCCInfo2);
  }
}
