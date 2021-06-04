package com.thirdcc.webapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventImageMapperTest {

  private EventImageMapper eventImageMapper;

  @BeforeEach
  public void setUp() {
    eventImageMapper = new EventImageMapperImpl();
  }
}
