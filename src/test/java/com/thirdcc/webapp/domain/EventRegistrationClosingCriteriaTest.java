package com.thirdcc.webapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventRegistrationClosingCriteriaTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(EventRegistrationClosingCriteria.class);
    EventRegistrationClosingCriteria eventRegistrationClosingCriteria1 = new EventRegistrationClosingCriteria();
    eventRegistrationClosingCriteria1.setId(1L);
    EventRegistrationClosingCriteria eventRegistrationClosingCriteria2 = new EventRegistrationClosingCriteria();
    eventRegistrationClosingCriteria2.setId(eventRegistrationClosingCriteria1.getId());
    assertThat(eventRegistrationClosingCriteria1).isEqualTo(eventRegistrationClosingCriteria2);
    eventRegistrationClosingCriteria2.setId(2L);
    assertThat(eventRegistrationClosingCriteria1).isNotEqualTo(eventRegistrationClosingCriteria2);
    eventRegistrationClosingCriteria1.setId(null);
    assertThat(eventRegistrationClosingCriteria1).isNotEqualTo(eventRegistrationClosingCriteria2);
  }
}
