package com.danigu.service.txstatistics.resources;

import java.time.temporal.TemporalAmount;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.danigu.service.txstatistics.core.EvictingTransactionStore;
import com.danigu.service.txstatistics.entities.api.ApiTransaction;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionsResource {
  private final TemporalAmount ageThreshold;
  private final EvictingTransactionStore transactionStore;

  /**
   * @param ageThreshold controls the acceptance of transactions, they must be younger than now - ageThreshold.
   * @param transactionStore is used to save transactions.
   */
  public TransactionsResource(final TemporalAmount ageThreshold, final EvictingTransactionStore transactionStore) {
    this.ageThreshold = ageThreshold;
    this.transactionStore = transactionStore;
  }

  @POST
  public void addTransaction(@Valid final ApiTransaction transaction, @Suspended final AsyncResponse response) {
    if (transaction.isOlderThan(ageThreshold)) {
      response.resume(Response.noContent().build());
      return;
    }

    transactionStore.addTransaction(transaction);
    response.resume(Response.status(Response.Status.CREATED).build());
  }

  @DELETE
  public void clearTransactions(@Suspended final AsyncResponse response) {
    transactionStore.evictAll();
    response.resume(Response.status(Response.Status.NO_CONTENT).build());
  }
}
