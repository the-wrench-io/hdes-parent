package io.resys.hdes.ui.quarkus.deployment.test;

/*-
 * #%L
 * hdes-ui-quarkus-deployment
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;


//-Djava.util.logging.manager=org.jboss.logmanager.LogManager
public class ExtensionTests {
  @RegisterExtension
  final static QuarkusUnitTest config = new QuarkusUnitTest()
      .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
          .addAsResource(new StringAsset("quarkus.hdes.enable=true"), "application.properties")
          .addAsResource(new StringAsset("quarkus.profile=test"), "application.properties")
          );

  @Test
  public void responsesIndex() {
    RestAssured.when().get("/hdes-ui").then().statusCode(200);
    RestAssured.when().get("/hdes-ui/index.html").then().statusCode(200);
  }
  
  @Test
  public void responsesDef() {
    RestAssured.when().get("/hdes-ui/services/defs").then().statusCode(200);
  }
  
  @Test
  public void responsesDebug() {
    RestAssured.given().body("{}").when().post("/hdes-ui/services/debug/superBranch/superResource").then().statusCode(200);
  }
}
