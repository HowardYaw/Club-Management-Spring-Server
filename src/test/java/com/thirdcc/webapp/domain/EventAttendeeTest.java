package com.thirdcc.webapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventAttendeeTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(EventAttendee.class);
    EventAttendee eventAttendee1 = new EventAttendee();
    eventAttendee1.setId(1L);
    EventAttendee eventAttendee2 = new EventAttendee();
    eventAttendee2.setId(eventAttendee1.getId());
    assertThat(eventAttendee1).isEqualTo(eventAttendee2);
    eventAttendee2.setId(2L);
    assertThat(eventAttendee1).isNotEqualTo(eventAttendee2);
    eventAttendee1.setId(null);
    assertThat(eventAttendee1).isNotEqualTo(eventAttendee2);
  }
}
