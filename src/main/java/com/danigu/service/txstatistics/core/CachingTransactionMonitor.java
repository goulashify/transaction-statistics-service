package com.danigu.service.txstatistics.core;

import java.time.Duration;
import java.util.DoubleSummaryStatistics;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import com.danigu.service.txstatistics.entities.api.ApiTransactionStatistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.lifecycle.Managed;

/**
 * Maintains a cache of the transaction statistics, it's recomputed based on the refreshInterval.
 */
@ThreadSafe
public class CachingTransactionMonitor implements Managed {
  private static final Logger logger = LoggerFactory.getLogger(CachingTransactionMonitor.class);
  private final Duration refreshInterval;
  private final EvictingTransactionStore transactionStore;
  private final ScheduledExecutorService refreshExecutor;
  private volatile ApiTransactionStatistics statistics = ApiTransactionStatistics.EMPTY;

  /**
   * @param transactionStore is used to retrieve transactions which we calculate statistics upon.
   * @param refreshInterval controls how often the statistics are recalculated.
   */
  public CachingTransactionMonitor(final EvictingTransactionStore transactionStore, final Duration refreshInterval) {
    this.transactionStore = transactionStore;
    this.refreshInterval = refreshInterval;
    this.refreshExecutor = Executors.newSingleThreadScheduledExecutor();
  }

  public ApiTransactionStatistics getStatistics() {
    return statistics;
  }

  public synchronized void recalculateStatistics() {
    final DoubleSummaryStatistics txStats = transactionStore.getTransactions().stream().mapToDouble(tx -> tx.amount).summaryStatistics();
    this.statistics = ApiTransactionStatistics.from(txStats);
  }

  @Override
  public void start() throws Exception {
    logger.info("Starting refresh executor at rate", refreshInterval);
    this.refreshExecutor.scheduleAtFixedRate(this::recalculateStatistics, 0, refreshInterval.toMillis(), TimeUnit.MILLISECONDS);
  }

  @Override
  public void stop() throws Exception {
    logger.trace("Trying to stop refresh executor");

    if (!this.refreshExecutor.isShutdown()) {
      logger.info("Stopping refresh executor");
      this.refreshExecutor.shutdown();
    }
  }
}
