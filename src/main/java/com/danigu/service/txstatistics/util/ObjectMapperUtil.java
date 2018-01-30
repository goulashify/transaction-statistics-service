package com.danigu.service.txstatistics.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.FuzzyEnumModule;
import io.dropwizard.jackson.GuavaExtrasModule;

public class ObjectMapperUtil {
  public static final ObjectMapper DEFAULT_OBJECT_MAPPER = configureMapper(new ObjectMapper());

  /**
   * @param mapper to configure.
   * @return a lenient object mapper, ready to use as Dropwizard's main mapper.
   */
  public static ObjectMapper configureMapper(final ObjectMapper mapper) {
    mapper.registerModule(new GuavaModule());
    mapper.registerModule(new GuavaExtrasModule());
    mapper.registerModule(new FuzzyEnumModule());
    mapper.setSubtypeResolver(new DiscoverableSubtypeResolver());

    mapper.registerModule(new ParameterNamesModule());
    mapper.registerModule(new Jdk8Module());
    mapper.registerModule(new JavaTimeModule());

    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
    mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    return mapper;
  }
}
