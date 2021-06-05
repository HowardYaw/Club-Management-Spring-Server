package com.thirdcc.webapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventImageTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(EventImage.class);
    EventImage eventImage1 = new EventImage();
    eventImage1.setId(1L);
    EventImage eventImage2 = new EventImage();
    eventImage2.setId(eventImage1.getId());
    assertThat(eventImage1).isEqualTo(eventImage2);
    eventImage2.setId(2L);
    assertThat(eventImage1).isNotEqualTo(eventImage2);
    eventImage1.setId(null);
    assertThat(eventImage1).isNotEqualTo(eventImage2);
  }
}
