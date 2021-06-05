package com.thirdcc.webapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventRegistrationClosingCriteriaMapperTest {

  private EventRegistrationClosingCriteriaMapper eventRegistrationClosingCriteriaMapper;

  @BeforeEach
  public void setUp() {
    eventRegistrationClosingCriteriaMapper = new EventRegistrationClosingCriteriaMapperImpl();
  }
}
