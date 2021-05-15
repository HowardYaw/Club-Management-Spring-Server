package com.thirdcc.webapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClubFamilyDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(ClubFamilyDTO.class);
    ClubFamilyDTO clubFamilyDTO1 = new ClubFamilyDTO();
    clubFamilyDTO1.setId(1L);
    ClubFamilyDTO clubFamilyDTO2 = new ClubFamilyDTO();
    assertThat(clubFamilyDTO1).isNotEqualTo(clubFamilyDTO2);
    clubFamilyDTO2.setId(clubFamilyDTO1.getId());
    assertThat(clubFamilyDTO1).isEqualTo(clubFamilyDTO2);
    clubFamilyDTO2.setId(2L);
    assertThat(clubFamilyDTO1).isNotEqualTo(clubFamilyDTO2);
    clubFamilyDTO1.setId(null);
    assertThat(clubFamilyDTO1).isNotEqualTo(clubFamilyDTO2);
  }
}
