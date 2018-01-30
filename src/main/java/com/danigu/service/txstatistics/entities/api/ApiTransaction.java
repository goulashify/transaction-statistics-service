package com.danigu.service.txstatistics.entities.api;

import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Objects;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class ApiTransaction {
  @NotNull
  @Min(value = 0, message = "Transaction amount can't be lower than 0.")
  public final double amount;

  @NotNull
  @JsonProperty("timestamp")
  public final Instant createdAt;

  @JsonCreator
  public ApiTransaction(final double amount, final Instant createdAt) {
    this.amount = amount;
    this.createdAt = createdAt;
  }

  /**
   * @param timeAmount  to subtract from the current time.
   * @return true if the createdAt is older than now - time amount, false if not.
   */
  public Boolean isOlderThan(final TemporalAmount timeAmount) {
    return this.createdAt.isBefore(Instant.now().minus(timeAmount));
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final ApiTransaction that = (ApiTransaction) o;
    return Double.compare(that.amount, amount) == 0 && Objects.equals(createdAt, that.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, createdAt);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("amount", amount)
        .add("timestamp", createdAt)
        .toString();
  }
}
