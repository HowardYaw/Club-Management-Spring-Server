package com.thirdcc.webapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventActivityDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(EventActivityDTO.class);
    EventActivityDTO eventActivityDTO1 = new EventActivityDTO();
    eventActivityDTO1.setId(1L);
    EventActivityDTO eventActivityDTO2 = new EventActivityDTO();
    assertThat(eventActivityDTO1).isNotEqualTo(eventActivityDTO2);
    eventActivityDTO2.setId(eventActivityDTO1.getId());
    assertThat(eventActivityDTO1).isEqualTo(eventActivityDTO2);
    eventActivityDTO2.setId(2L);
    assertThat(eventActivityDTO1).isNotEqualTo(eventActivityDTO2);
    eventActivityDTO1.setId(null);
    assertThat(eventActivityDTO1).isNotEqualTo(eventActivityDTO2);
  }
}
