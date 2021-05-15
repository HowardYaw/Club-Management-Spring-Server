package com.thirdcc.webapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventAttendeeMapperTest {

  private EventAttendeeMapper eventAttendeeMapper;

  @BeforeEach
  public void setUp() {
    eventAttendeeMapper = new EventAttendeeMapperImpl();
  }
}
