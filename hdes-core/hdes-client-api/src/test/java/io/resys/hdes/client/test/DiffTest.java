package io.resys.hdes.client.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.spi.util.FileUtils;
import io.resys.hdes.client.test.config.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DiffTest {

  @Test
  public void diffTwoReleasesTest() throws JsonProcessingException {
    final var tags = List.of(
        TestUtils.fileToStoreEntity("diff/v1.json", AstBodyType.TAG),
        TestUtils.fileToStoreEntity("diff/v2.json", AstBodyType.TAG)
    );

    final var diff = TestUtils.client.diff()
        .baseId("2eeb259a-d15e-4c7a-b1cb-ce18a3ef384b")
        .targetId("b4238435-7c87-44fa-8fb5-7bbdbe8e2d24")
        .targetDate(TestUtils.targetDate)
        .tags(tags)
        .build();

    Assertions.assertEquals(
        FileUtils.toString(getClass(), "diff/diffResult.json"),
        TestUtils.objectMapper.valueToTree(diff).toString()
    );
  }
}
