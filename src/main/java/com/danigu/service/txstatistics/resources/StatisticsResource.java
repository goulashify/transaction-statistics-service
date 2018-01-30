package com.danigu.service.txstatistics.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.danigu.service.txstatistics.core.CachingTransactionMonitor;

@Path("/statistics")
@Produces(MediaType.APPLICATION_JSON)
public class StatisticsResource {
  private final CachingTransactionMonitor statisticsService;

  /**
   * @param statisticsService is used to get the transaction statistics.
   */
  public StatisticsResource(final CachingTransactionMonitor statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GET
  public void getStatistics(@Suspended final AsyncResponse response) {
    response.resume(Response.ok(statisticsService.getStatistics()).build());
  }

  @POST
  @Path("/refresh")
  public void refreshStatistics(@Suspended final AsyncResponse response) {
    statisticsService.recalculateStatistics();
    response.resume(Response.ok(statisticsService.getStatistics()).build());
  }
}
