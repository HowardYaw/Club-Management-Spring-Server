package com.thirdcc.webapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserUniInfoTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(UserUniInfo.class);
    UserUniInfo userUniInfo1 = new UserUniInfo();
    userUniInfo1.setId(1L);
    UserUniInfo userUniInfo2 = new UserUniInfo();
    userUniInfo2.setId(userUniInfo1.getId());
    assertThat(userUniInfo1).isEqualTo(userUniInfo2);
    userUniInfo2.setId(2L);
    assertThat(userUniInfo1).isNotEqualTo(userUniInfo2);
    userUniInfo1.setId(null);
    assertThat(userUniInfo1).isNotEqualTo(userUniInfo2);
  }
}
