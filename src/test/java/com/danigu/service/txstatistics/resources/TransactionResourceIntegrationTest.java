package com.danigu.service.txstatistics.resources;

import java.time.Duration;
import java.time.Instant;

import com.danigu.service.txstatistics.entities.api.ApiTransaction;

import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

public class TransactionResourceIntegrationTest extends BaseIntegrationTest {
  private static final String TRANSACTIONS_ENDPOINT = "/transactions";

  @Test
  public void invalidTransactionsCanNotBeSubmitted() {
    postTransaction(new ApiTransaction(-1, null))
      .then()
        .statusCode(422);
  }

  @Test
  public void expiredTransactionsCanNotBeSubmitted() {
    final Duration ageThresholdPlusFiveMinutes = TEST_SUPPORT.getConfiguration().ageThreshold.plusMinutes(5);

    postTransaction(new ApiTransaction(120, Instant.now().minus(ageThresholdPlusFiveMinutes)))
        .then()
        .statusCode(204);
  }

  @Test
  public void transactionsCanBeSubmitted() {
    postTransaction(new ApiTransaction(120, Instant.now()))
        .then()
        .statusCode(201);
  }

  @Test
  public void futureTransactionsCanBeSubmitted() {
    postTransaction(new ApiTransaction(120, Instant.now().plus(Duration.ofMinutes(5))))
        .then()
        .statusCode(201);
  }

  private Response postTransaction(final ApiTransaction transaction) {
    return given().contentType(ContentType.JSON).when().body(transaction).post(TRANSACTIONS_ENDPOINT);
  }
}
