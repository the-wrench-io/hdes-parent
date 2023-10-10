package io.resys.hdes.client.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.resys.hdes.client.api.ast.AstBody;
import io.resys.hdes.client.spi.util.FileUtils;
import io.resys.hdes.client.test.config.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SummaryTest {

  @Test
  public void createTagSummaryTest() throws JsonProcessingException {
    final var tags = List.of(
        TestUtils.fileToStoreEntity("diff/v1.json", AstBody.AstBodyType.TAG),
        TestUtils.fileToStoreEntity("diff/v2.json", AstBody.AstBodyType.TAG)
    );

    final var v1Summary = TestUtils.client.summary()
        .tags(tags)
        .tagId("2eeb259a-d15e-4c7a-b1cb-ce18a3ef384b")
        .build();

    Assertions.assertEquals(
        FileUtils.toString(getClass(), "summary/summaryResult.json"),
        TestUtils.objectMapper.valueToTree(v1Summary).toString()
    );
  }
}
