package com.thirdcc.webapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventCrewTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(EventCrew.class);
    EventCrew eventCrew1 = new EventCrew();
    eventCrew1.setId(1L);
    EventCrew eventCrew2 = new EventCrew();
    eventCrew2.setId(eventCrew1.getId());
    assertThat(eventCrew1).isEqualTo(eventCrew2);
    eventCrew2.setId(2L);
    assertThat(eventCrew1).isNotEqualTo(eventCrew2);
    eventCrew1.setId(null);
    assertThat(eventCrew1).isNotEqualTo(eventCrew2);
  }
}
