package com.danigu.service.txstatistics;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.dropwizard.Configuration;

public class TransactionStatisticsServiceConfiguration extends Configuration {
  public final Duration ageThreshold;
  public final Duration statisticsRefreshInterval;

  @JsonCreator
  public TransactionStatisticsServiceConfiguration(final long ageThresholdMs, final long statisticsRefreshIntervalMs) {
    this.ageThreshold = Duration.ofMillis(ageThresholdMs);
    this.statisticsRefreshInterval = Duration.ofMillis(statisticsRefreshIntervalMs);
  }
}
