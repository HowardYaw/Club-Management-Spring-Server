package com.thirdcc.webapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventActivityMapperTest {

  private EventActivityMapper eventActivityMapper;

  @BeforeEach
  public void setUp() {
    eventActivityMapper = new EventActivityMapperImpl();
  }
}
