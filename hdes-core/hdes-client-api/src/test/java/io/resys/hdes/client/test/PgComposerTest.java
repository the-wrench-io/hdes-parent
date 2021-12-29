package io.resys.hdes.client.test;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.resys.hdes.client.api.ImmutableCreateEntity;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.ImmutableAstCommand;
import io.resys.hdes.client.spi.HdesComposerImpl;
import io.resys.hdes.client.spi.util.FileUtils;
import io.resys.hdes.client.test.config.PgProfile;
import io.resys.hdes.client.test.config.PgTestTemplate;
import io.resys.hdes.client.test.config.TestUtils;

@QuarkusTest
@TestProfile(PgProfile.class)
public class PgComposerTest extends PgTestTemplate {

  @Test
  public void readWriteRunTest() {
    final var client = getClient().repo().repoName("PgComposerTest").create()
        .await().atMost(Duration.ofMinutes(1));
    final var composer = new HdesComposerImpl(client);       

    composer.create(ImmutableCreateEntity.builder()
        .addBody(
            ImmutableAstCommand.builder()
              .type(AstCommandValue.SET_BODY)
              .value(FileUtils.toString(getClass(), "pg_test/pg-aml-flow.txt"))
        .build())
        .type(AstBodyType.FLOW)
        .build())
    .await().atMost(Duration.ofMinutes(1));
    
    composer.create(ImmutableCreateEntity.builder()
        .body(getCommands("pg_test/pg-dt.json"))
        .type(AstBodyType.DT)
        .build())
    .await().atMost(Duration.ofMinutes(1));
    
    composer.create(ImmutableCreateEntity.builder()
        .addBody(
            ImmutableAstCommand.builder()
              .type(AstCommandValue.SET_BODY)
              .value(FileUtils.toString(getClass(), "pg_test/PgTestService.txt"))
              .build())
        .type(AstBodyType.FLOW_TASK)
        .build())
    .await().atMost(Duration.ofMinutes(1));
    
    
  }
  
  
  public static List<AstCommand> getCommands(String fileName) {
    try {
      final var data = FileUtils.toString(PgComposerTest.class, fileName);
      return TestUtils.client.mapper().commandsList(data);
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

}