package io.resys.hdes.ui.quarkus.deployment.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;

public class ExtensionTests {
  @RegisterExtension
  final static QuarkusUnitTest config = new QuarkusUnitTest()
      .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

  @Test
  public void responses() {
    RestAssured.when().get("/hdes-ui").then().statusCode(200);
    RestAssured.when().get("/hdes-ui/index.html").then().statusCode(200);
  }
}
