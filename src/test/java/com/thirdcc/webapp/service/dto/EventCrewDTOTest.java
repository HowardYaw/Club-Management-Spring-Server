package com.thirdcc.webapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventCrewDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(EventCrewDTO.class);
    EventCrewDTO eventCrewDTO1 = new EventCrewDTO();
    eventCrewDTO1.setId(1L);
    EventCrewDTO eventCrewDTO2 = new EventCrewDTO();
    assertThat(eventCrewDTO1).isNotEqualTo(eventCrewDTO2);
    eventCrewDTO2.setId(eventCrewDTO1.getId());
    assertThat(eventCrewDTO1).isEqualTo(eventCrewDTO2);
    eventCrewDTO2.setId(2L);
    assertThat(eventCrewDTO1).isNotEqualTo(eventCrewDTO2);
    eventCrewDTO1.setId(null);
    assertThat(eventCrewDTO1).isNotEqualTo(eventCrewDTO2);
  }
}
