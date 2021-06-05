package com.thirdcc.webapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventAttendeeDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(EventAttendeeDTO.class);
    EventAttendeeDTO eventAttendeeDTO1 = new EventAttendeeDTO();
    eventAttendeeDTO1.setId(1L);
    EventAttendeeDTO eventAttendeeDTO2 = new EventAttendeeDTO();
    assertThat(eventAttendeeDTO1).isNotEqualTo(eventAttendeeDTO2);
    eventAttendeeDTO2.setId(eventAttendeeDTO1.getId());
    assertThat(eventAttendeeDTO1).isEqualTo(eventAttendeeDTO2);
    eventAttendeeDTO2.setId(2L);
    assertThat(eventAttendeeDTO1).isNotEqualTo(eventAttendeeDTO2);
    eventAttendeeDTO1.setId(null);
    assertThat(eventAttendeeDTO1).isNotEqualTo(eventAttendeeDTO2);
  }
}
