package com.thirdcc.webapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventActivityTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(EventActivity.class);
    EventActivity eventActivity1 = new EventActivity();
    eventActivity1.setId(1L);
    EventActivity eventActivity2 = new EventActivity();
    eventActivity2.setId(eventActivity1.getId());
    assertThat(eventActivity1).isEqualTo(eventActivity2);
    eventActivity2.setId(2L);
    assertThat(eventActivity1).isNotEqualTo(eventActivity2);
    eventActivity1.setId(null);
    assertThat(eventActivity1).isNotEqualTo(eventActivity2);
  }
}
