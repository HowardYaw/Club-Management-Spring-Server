package com.thirdcc.webapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImageStorageMapperTest {

  private ImageStorageMapper imageStorageMapper;

  @BeforeEach
  public void setUp() {
    imageStorageMapper = new ImageStorageMapperImpl();
  }
}
