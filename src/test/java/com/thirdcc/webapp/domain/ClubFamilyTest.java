package com.thirdcc.webapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClubFamilyTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(ClubFamily.class);
    ClubFamily clubFamily1 = new ClubFamily();
    clubFamily1.setId(1L);
    ClubFamily clubFamily2 = new ClubFamily();
    clubFamily2.setId(clubFamily1.getId());
    assertThat(clubFamily1).isEqualTo(clubFamily2);
    clubFamily2.setId(2L);
    assertThat(clubFamily1).isNotEqualTo(clubFamily2);
    clubFamily1.setId(null);
    assertThat(clubFamily1).isNotEqualTo(clubFamily2);
  }
}
