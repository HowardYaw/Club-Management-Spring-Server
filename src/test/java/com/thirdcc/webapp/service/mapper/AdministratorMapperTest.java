package com.thirdcc.webapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdministratorMapperTest {

  private AdministratorMapper administratorMapper;

  @BeforeEach
  public void setUp() {
    administratorMapper = new AdministratorMapperImpl();
  }
}
