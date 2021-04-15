package io.resys.hdes.docdb.tests;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.docdb.api.actions.RepoActions.RepoResult;
import io.resys.hdes.docdb.api.actions.RepoActions.RepoStatus;
import io.resys.hdes.docdb.tests.config.MongoDbConfig;


public class SimpleTest extends MongoDbConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTest.class);
  
  @Test
  public void crateRepo() {
    RepoResult repo = getClient().repo().create().name("project-x").build()
        .await().atMost(Duration.ofMinutes(1));
    
    Assertions.assertEquals(RepoStatus.OK, repo.getStatus());
    
  }
}
