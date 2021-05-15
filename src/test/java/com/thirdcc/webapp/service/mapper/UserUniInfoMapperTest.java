package com.thirdcc.webapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserUniInfoMapperTest {

  private UserUniInfoMapper userUniInfoMapper;

  @BeforeEach
  public void setUp() {
    userUniInfoMapper = new UserUniInfoMapperImpl();
  }
}
