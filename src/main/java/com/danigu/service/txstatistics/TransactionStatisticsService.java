package com.danigu.service.txstatistics;

import com.danigu.service.txstatistics.core.CachingTransactionMonitor;
import com.danigu.service.txstatistics.core.EvictingTransactionStore;
import com.danigu.service.txstatistics.resources.StatisticsResource;
import com.danigu.service.txstatistics.resources.TransactionsResource;
import com.danigu.service.txstatistics.util.ObjectMapperUtil;

import io.dropwizard.Application;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TransactionStatisticsService extends Application<TransactionStatisticsServiceConfiguration> {

    public static void main(final String[] args) throws Exception {
        new TransactionStatisticsService().run(args);
    }

    @Override
    public void initialize(final Bootstrap<TransactionStatisticsServiceConfiguration> bootstrap) {
      bootstrap.setObjectMapper(ObjectMapperUtil.DEFAULT_OBJECT_MAPPER);
    }

    @Override
    public void run(final TransactionStatisticsServiceConfiguration configuration, final Environment environment) {
        // Services.
        final EvictingTransactionStore txStore = new EvictingTransactionStore(configuration.ageThreshold);
        final CachingTransactionMonitor txMonitor = new CachingTransactionMonitor(txStore, configuration.statisticsRefreshInterval);

        environment.lifecycle().manage(txMonitor);

        // Resources.
        final StatisticsResource statisticsResource = new StatisticsResource(txMonitor);
        final TransactionsResource transactionsResource = new TransactionsResource(configuration.ageThreshold, txStore);

        environment.jersey().register(statisticsResource);
        environment.jersey().register(transactionsResource);

        // Exception mappers.
        environment.jersey().register(new JsonProcessingExceptionMapper(true));
    }

}
