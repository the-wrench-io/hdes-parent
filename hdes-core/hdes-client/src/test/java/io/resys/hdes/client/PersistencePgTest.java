package io.resys.hdes.client;

/*-
 * #%L
 * stencil-persistence
 * %%
 * Copyright (C) 2021 Copyright 2021 ReSys OÜ
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

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.resys.hdes.client.api.HdesStore.Entity;
import io.resys.hdes.client.api.ImmutableDeleteAstType;
import io.resys.hdes.client.api.ImmutableUpdateAstType;
import io.resys.hdes.client.api.ast.AstBody.EntityType;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.ImmutableAstCommand;
import io.resys.hdes.client.config.PgProfile;
import io.resys.hdes.client.config.PgTestTemplate;
import io.resys.hdes.client.config.RepositoryToStaticData;

@QuarkusTest
@TestProfile(PgProfile.class)
public class PersistencePgTest extends PgTestTemplate {

  @Disabled
  @Test
  public void starter() {
    final var repo = getHdes("test1");
    
    Entity<AstFlow> article1 = repo.store().create().flow("first-flow")
      .onFailure().invoke(e -> e.printStackTrace()).onFailure().recoverWithNull()
      .await().atMost(Duration.ofMinutes(1));

    // create state
    var expected = RepositoryToStaticData.toString(getClass(), "create_state.txt");
    var actual = super.toRepoExport("test1");
    Assertions.assertEquals(expected, actual);
    
    repo.store().update()
      .build(ImmutableUpdateAstType.builder()
          .id(article1.getId())
          .type(EntityType.FLOW)
          .addBody(ImmutableAstCommand.builder()
              .type(AstCommandValue.SET_BODY)
              .value("id: change flow symbolic id")
              .build())
          .build())
      .onFailure().invoke(e -> e.printStackTrace()).onFailure().recoverWithNull()
      .await().atMost(Duration.ofMinutes(1));
    
    
    // update state
    expected = RepositoryToStaticData.toString(getClass(), "update_state.txt");
    actual = super.toRepoExport("test1");
    Assertions.assertEquals(expected, actual);
    
    
    repo.store().delete()
      .build(ImmutableDeleteAstType.builder().id(article1.getId()).build())
      .onFailure().invoke(e -> e.printStackTrace()).onFailure().recoverWithNull()
      .await().atMost(Duration.ofMinutes(1));
    
    // delete state
    expected = RepositoryToStaticData.toString(getClass(), "delete_state.txt");
    actual = super.toRepoExport("test1");
    Assertions.assertEquals(expected, actual);
    
  }

}
