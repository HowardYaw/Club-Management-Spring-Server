package com.thirdcc.webapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventRegistrationClosingCriteriaDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(EventRegistrationClosingCriteriaDTO.class);
    EventRegistrationClosingCriteriaDTO eventRegistrationClosingCriteriaDTO1 = new EventRegistrationClosingCriteriaDTO();
    eventRegistrationClosingCriteriaDTO1.setId(1L);
    EventRegistrationClosingCriteriaDTO eventRegistrationClosingCriteriaDTO2 = new EventRegistrationClosingCriteriaDTO();
    assertThat(eventRegistrationClosingCriteriaDTO1).isNotEqualTo(eventRegistrationClosingCriteriaDTO2);
    eventRegistrationClosingCriteriaDTO2.setId(eventRegistrationClosingCriteriaDTO1.getId());
    assertThat(eventRegistrationClosingCriteriaDTO1).isEqualTo(eventRegistrationClosingCriteriaDTO2);
    eventRegistrationClosingCriteriaDTO2.setId(2L);
    assertThat(eventRegistrationClosingCriteriaDTO1).isNotEqualTo(eventRegistrationClosingCriteriaDTO2);
    eventRegistrationClosingCriteriaDTO1.setId(null);
    assertThat(eventRegistrationClosingCriteriaDTO1).isNotEqualTo(eventRegistrationClosingCriteriaDTO2);
  }
}
