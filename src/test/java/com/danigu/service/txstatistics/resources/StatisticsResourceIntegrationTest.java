package com.danigu.service.txstatistics.resources;

import java.time.Duration;
import java.time.Instant;

import com.danigu.service.txstatistics.entities.api.ApiTransaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class StatisticsResourceIntegrationTest extends BaseIntegrationTest {
  private static final String STATISTICS_ENDPOINT = "/statistics";
  private static final String STATISTICS_REFRESH_ENDPOINT = "/statistics/refresh";
  private static final String TRANSACTIONS_ENDPOINT = "/transactions";

  private Duration ageThreshold = TEST_SUPPORT.getConfiguration().ageThreshold;

  @BeforeEach
  public void evictAndRecalculate() {
    given().contentType(ContentType.JSON).when().delete(TRANSACTIONS_ENDPOINT).then().statusCode(204);
    given().contentType(ContentType.JSON).when().post(STATISTICS_REFRESH_ENDPOINT).then().statusCode(200);
  }

  @Test
  public void emptyIsReturnedWhenNoTransactionsExist() {
    getStatistics()
      .then()
        .statusCode(200)
        .body("sum", equalTo(0F))
        .body("avg", equalTo(0F))
        .body("max", equalTo(0F))
        .body("min", equalTo(0F))
        .body("count", equalTo(0));
  }

  @Test
  public void validResultsAreReturnedWhenTransactionsExist() {
    postTransactions(
        new ApiTransaction(100, Instant.now()),
        new ApiTransaction(200, Instant.now()),
        new ApiTransaction(400, Instant.now()),
        new ApiTransaction(800, Instant.now())
    );

    waitForRefresh();

    getStatistics()
      .then()
        .statusCode(200)
        .body("sum", is(1500F))
        .body("avg", is(375F))
        .body("max", is(800F))
        .body("min", is(100F))
        .body("count", is(4));
  }

  @Test
  public void transactionsAreEvictedAsWindowSlides() {
    postTransactions(
        new ApiTransaction(100, Instant.now()), // Will be valid now, but expire after expire.
        new ApiTransaction(200, Instant.now().plus(ageThreshold.multipliedBy(2))) // Will be valid now and after expire.
    );

    waitForRefresh();

    // Both transactions are expected here.
    getStatistics()
      .then()
        .statusCode(200)
        .body("sum", is(300F))
        .body("avg", is(150F))
        .body("max", is(200F))
        .body("min", is(100F))
        .body("count", is(2));

    waitForEvictionRound();

    // Only the second transaction is expected here.
    getStatistics()
      .then()
        .statusCode(200)
        .body("sum", is(200F))
        .body("avg", is(200F))
        .body("max", is(200F))
        .body("min", is(200F))
        .body("count", is(1));
  }

  @Test
  public void emptyIsReturnedWhenAllTransactionsEvicted() {
    postTransactions(
        new ApiTransaction(400, Instant.now()),
        new ApiTransaction(600, Instant.now())
    );

    waitForRefresh();

    // Both transactions are expected here.
    getStatistics()
      .then()
        .statusCode(200)
        .body("sum", is(1000F))
        .body("avg", is(500F))
        .body("max", is(600F))
        .body("min", is(400F))
        .body("count", is(2));

    waitForEvictionRound();

    // Empty result is expected - all transactions are expired.
    getStatistics()
      .then()
        .statusCode(200)
        .body("sum", is(0F))
        .body("avg", is(0F))
        .body("max", is(0F))
        .body("min", is(0F))
        .body("count", is(0));
  }

  public void waitForRefresh() {
    try {
      Thread.sleep(TEST_SUPPORT.getConfiguration().statisticsRefreshInterval.toMillis());
    } catch(final InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void waitForEvictionRound() {
    try {
      Thread.sleep(TEST_SUPPORT.getConfiguration().ageThreshold.toMillis());
    } catch(final InterruptedException e) {
      throw new RuntimeException(e);
    }
  }


  private Response getStatistics() {
    return given().contentType(ContentType.JSON).when().get(STATISTICS_ENDPOINT);
  }

  private void postTransactions(final ApiTransaction... transactions) {
    for (final ApiTransaction transaction : transactions) {
      given()
          .contentType(ContentType.JSON)
        .when()
          .body(transaction)
          .post(TRANSACTIONS_ENDPOINT)
        .then()
          .statusCode(201);
    }
  }
}
