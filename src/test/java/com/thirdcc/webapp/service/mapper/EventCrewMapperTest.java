package com.thirdcc.webapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventCrewMapperTest {

  private EventCrewMapper eventCrewMapper;

  @BeforeEach
  public void setUp() {
    eventCrewMapper = new EventCrewMapperImpl();
  }
}
