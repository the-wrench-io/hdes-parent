package io.resys.hdes.docdb.tests;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.docdb.tests.config.MongoDbConfig;


public class SimpleTest extends MongoDbConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTest.class);
  
  @Test
  public void connection() {
    
  }
}
