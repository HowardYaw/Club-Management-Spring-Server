package com.thirdcc.webapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClubFamilyMapperTest {

  private ClubFamilyMapper clubFamilyMapper;

  @BeforeEach
  public void setUp() {
    clubFamilyMapper = new ClubFamilyMapperImpl();
  }
}
