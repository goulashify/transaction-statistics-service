package com.danigu.service.txstatistics.core;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.danigu.service.txstatistics.entities.api.ApiTransaction;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.google.common.collect.ImmutableList;

/**
 * Stores {@link ApiTransaction transactions} and automatically removes them after they expire.
 */
@ThreadSafe
public class EvictingTransactionStore {
  private final Cache<UUID, ApiTransaction> cache;
  private final Map<UUID, ApiTransaction> cacheView;

  /**
   * @param ageThreshold controls when a transaction should be evicted
   * @see EvictionPolicy
   */
  public EvictingTransactionStore(final TemporalAmount ageThreshold) {
    this.cache = Caffeine.newBuilder().expireAfter(new EvictionPolicy(ageThreshold)).build();
    this.cacheView = cache.asMap();
  }

  public void addTransaction(final ApiTransaction transaction) {
    cache.put(UUID.randomUUID(), transaction);
  }

  public ImmutableList<ApiTransaction> getTransactions() {
    cache.cleanUp();
    return ImmutableList.copyOf(cacheView.values());
  }

  public void evictAll() {
    this.cache.invalidateAll();
    this.cache.cleanUp();
  }

  /**
   * Custom eviction policy for {@link ApiTransaction} caches, each value is expired after the {@link ApiTransaction#createdAt} becomes
   * older than now - age threshold.
   */
  public static class EvictionPolicy implements Expiry<UUID, ApiTransaction> {
    public final TemporalAmount ageThreshold;

    public EvictionPolicy(final TemporalAmount ageThreshold) {
      this.ageThreshold = ageThreshold;
    }

    @Override
    public long expireAfterCreate(@Nonnull final UUID key, @Nonnull final ApiTransaction tx, final long currentTime) {
      final Instant now = Instant.now();
      final Instant startOfWindow = now.minus(ageThreshold);

      if (tx.createdAt.isBefore(startOfWindow)) {
        return 0; // Should be evicted right away, it was expired the time it was created.
      }

      return Duration.between(startOfWindow, tx.createdAt).toNanos();
    }

    @Override
    public long expireAfterUpdate(@Nonnull final UUID key, @Nonnull final ApiTransaction value, final long currentTime, final long currentDuration) {
      return currentDuration;
    }

    @Override
    public long expireAfterRead(@Nonnull final UUID key, @Nonnull final ApiTransaction value, final long currentTime, final long currentDuration) {
      return currentDuration;
    }
  }
}
