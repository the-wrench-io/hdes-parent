package io.resys.hdes.docdb.tests;

import java.io.Serializable;
import java.time.Duration;

import org.immutables.value.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.docdb.api.actions.CommitActions.CommitResult;
import io.resys.hdes.docdb.api.actions.CommitActions.CommitStatus;
import io.resys.hdes.docdb.api.actions.RepoActions.RepoResult;
import io.resys.hdes.docdb.api.actions.RepoActions.RepoStatus;
import io.resys.hdes.docdb.tests.config.MongoDbConfig;


public class SimpleTest extends MongoDbConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTest.class);
  
  @Value.Immutable
  public interface TestContent extends Serializable {
    String getId();
    String getName();
  }
  
  @Test
  public void crateRepo() {
    // create project
    RepoResult repo = getClient().repo().create()
        .name("project-x")
        .build()
        .await().atMost(Duration.ofMinutes(1));
    LOGGER.debug("created repo {}", repo);
    Assertions.assertEquals(RepoStatus.OK, repo.getStatus());
    
    // Create head and first commit
    CommitResult commit = getClient().commit().head()
      .head("project-x", "main")
      .append("readme.md", "readme content")
      .append("file1.json", "[{}]")
      .append("fileFromObject.txt", ImmutableTestContent.builder().id("10").name("sam vimes").build().toString())
      .author("same vimes")
      .head(repo.getRepo().getId(), "main")
      .message("first commit!")
      .build()
      .await().atMost(Duration.ofMinutes(1));
    LOGGER.debug("created commit {}", commit);
    Assertions.assertEquals(CommitStatus.OK, commit.getStatus());
    
  }
}
