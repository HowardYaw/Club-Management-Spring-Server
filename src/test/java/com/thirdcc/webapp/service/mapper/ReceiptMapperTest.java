package com.thirdcc.webapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReceiptMapperTest {

  private ReceiptMapper receiptMapper;

  @BeforeEach
  public void setUp() {
    receiptMapper = new ReceiptMapperImpl();
  }
}
