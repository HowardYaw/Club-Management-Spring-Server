package com.thirdcc.webapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdcc.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReceiptTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(Receipt.class);
    Receipt receipt1 = new Receipt();
    receipt1.setId(1L);
    Receipt receipt2 = new Receipt();
    receipt2.setId(receipt1.getId());
    assertThat(receipt1).isEqualTo(receipt2);
    receipt2.setId(2L);
    assertThat(receipt1).isNotEqualTo(receipt2);
    receipt1.setId(null);
    assertThat(receipt1).isNotEqualTo(receipt2);
  }
}
