package com.danigu.service.txstatistics.resources;

import com.danigu.service.txstatistics.TransactionStatisticsService;
import com.danigu.service.txstatistics.TransactionStatisticsServiceConfiguration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import io.dropwizard.testing.DropwizardTestSupport;
import io.dropwizard.testing.ResourceHelpers;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;

public abstract class BaseIntegrationTest {
  protected static final DropwizardTestSupport<TransactionStatisticsServiceConfiguration> TEST_SUPPORT =
      new DropwizardTestSupport<>(TransactionStatisticsService.class, ResourceHelpers.resourceFilePath("test-config.yml"));

  @BeforeAll
  public static void setUp() {
    TEST_SUPPORT.before();

    RestAssured.baseURI = String.format("http://127.0.0.1:%d", TEST_SUPPORT.getLocalPort());
    RestAssured.config = RestAssuredConfig
        .config()
        .objectMapperConfig(
            new ObjectMapperConfig()
                .jackson2ObjectMapperFactory((clazz, s) -> TEST_SUPPORT.getObjectMapper())
        );
  }

  @AfterAll
  public static void tearDown() {
    TEST_SUPPORT.after();
  }

}
