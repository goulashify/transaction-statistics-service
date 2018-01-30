package com.danigu.service.txstatistics.entities.api;

import java.util.DoubleSummaryStatistics;
import java.util.Objects;

import com.google.common.base.MoreObjects;

/**
 * Statistics response class, summarizes statistics for all transactions in a time-window.
 */
public class ApiTransactionStatistics {
  public static ApiTransactionStatistics EMPTY = new ApiTransactionStatistics(0, 0, 0, 0, 0);

  public final double sum;
  public final double avg;
  public final double max;
  public final double min;
  public final long count;

  /**
   * @param sum is the sum of all transaction amounts in a given window.
   * @param avg is the average of all transaction amounts in a given window.
   * @param max is the maximum of all transaction amounts in a given window.
   * @param min is the minimum of all transaction amounts in a given window.
   * @param count is the count of transactions in a given window.
   */
  public ApiTransactionStatistics(final double sum, final double avg, final double max, final double min, final long count) {
    this.sum = sum;
    this.avg = avg;
    this.max = max;
    this.min = min;
    this.count = count;
  }

  public static ApiTransactionStatistics from(final DoubleSummaryStatistics txStats) {
    if (txStats.getCount() == 0) {
      return EMPTY;
    }

    return new ApiTransactionStatistics(txStats.getSum(), txStats.getAverage(), txStats.getMax(), txStats.getMin(), txStats.getCount());
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final ApiTransactionStatistics that = (ApiTransactionStatistics) o;
    return Double.compare(that.sum, sum) == 0 &&
        Double.compare(that.avg, avg) == 0 &&
        Double.compare(that.max, max) == 0 &&
        Double.compare(that.min, min) == 0 &&
        count == that.count;
  }

  @Override
  public int hashCode() {
    return Objects.hash(sum, avg, max, min, count);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("sum", sum)
        .add("avg", avg)
        .add("max", max)
        .add("min", min)
        .add("count", count)
        .toString();
  }
}
