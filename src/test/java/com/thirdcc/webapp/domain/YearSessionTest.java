package com.thirdcc.webapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class YearSessionTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(YearSession.class);
    YearSession yearSession1 = new YearSession();
    yearSession1.setId(1L);
    YearSession yearSession2 = new YearSession();
    yearSession2.setId(yearSession1.getId());
    assertThat(yearSession1).isEqualTo(yearSession2);
    yearSession2.setId(2L);
    assertThat(yearSession1).isNotEqualTo(yearSession2);
    yearSession1.setId(null);
    assertThat(yearSession1).isNotEqualTo(yearSession2);
  }
}
