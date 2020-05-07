package io.resys.hdes.ui.quarkus.deployment.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;

public class DisabledTest {
  @RegisterExtension
  final static QuarkusUnitTest config = new QuarkusUnitTest()
      .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
          .addAsResource(new StringAsset("quarkus.hdes-ui.enable=false"), "application.properties"));

  @Test
  public void shouldUseDefaultConfig() {
    RestAssured.when().get("/hdes-ui").then().statusCode(404);
    RestAssured.when().get("/hdes-ui/index.html").then().statusCode(404);
  }
}
