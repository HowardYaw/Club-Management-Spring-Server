package com.thirdcc.webapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserCCInfoMapperTest {

  private UserCCInfoMapper userCCInfoMapper;

  @BeforeEach
  public void setUp() {
    userCCInfoMapper = new UserCCInfoMapperImpl();
  }
}
