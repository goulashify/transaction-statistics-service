package com.danigu.service.txstatistics.entities.api;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import com.danigu.service.txstatistics.util.ObjectMapperUtil;

import org.junit.jupiter.api.Test;

import io.dropwizard.testing.ResourceHelpers;

import static org.junit.jupiter.api.Assertions.*;

public class ApiTransactionTest {
  final static File serializedTransaction = new File(ResourceHelpers.resourceFilePath("transaction.json"));
  final static double expectedAmount = 12.3;
  final static Instant expectedTimestamp = Instant.ofEpochMilli(1478192204000L);

  @Test
  public void testDeserialization() throws IOException {
    final ApiTransaction transaction = ObjectMapperUtil.DEFAULT_OBJECT_MAPPER.readValue(serializedTransaction, ApiTransaction.class);

    assertEquals(transaction.amount, expectedAmount);
    assertEquals(transaction.createdAt, expectedTimestamp);
  }
}
