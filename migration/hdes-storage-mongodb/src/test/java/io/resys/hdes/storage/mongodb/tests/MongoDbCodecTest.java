package io.resys.hdes.storage.mongodb.tests;

/*-
 * #%L
 * hdes-storage-mongodb
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

import static io.resys.hdes.storage.mongodb.tests.MongoDbConfig.client;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import io.resys.hdes.datatype.api.ImmutableDataTypeCommand;
import io.resys.hdes.storage.api.Changes;
import io.resys.hdes.storage.api.ImmutableChanges;

public class MongoDbCodecTest {
  @Test
  public void read() {
    client(mongo -> {
      Changes changesToStore = ImmutableChanges.builder()
          .tenant("testTenant")
          .label("test")
          .id("1")
          .values(Arrays.asList(
              ImmutableDataTypeCommand.builder()
                  .type(TestCommandType.TEST_COMMAND.toString())
                  .build(),
              ImmutableDataTypeCommand.builder()
                  .type(TestCommandType.TEST_COMMAND.toString())
                  .subType(TestCommandSubType.TEST_SUB_COMMAND.toString())
                  .build(),
              ImmutableDataTypeCommand.builder()
                  .id(1)
                  .type(TestCommandType.TEST_COMMAND.toString())
                  .subType(TestCommandSubType.TEST_SUB_COMMAND.toString())
                  .build(),
              ImmutableDataTypeCommand.builder()
                  .id(1)
                  .value("xxx")
                  .type(TestCommandType.TEST_COMMAND.toString())
                  .subType(TestCommandSubType.TEST_SUB_COMMAND.toString())
                  .build()))
          .build();
      
      mongo.getDatabase("XXX").getCollection("changes", Changes.class).insertOne(changesToStore);
      Changes actual = mongo.getDatabase("XXX").getCollection("changes", Changes.class).find().first();
      TestAssert.assertEquals("MongoDbCodecTest.json", actual);
      
    });
  }
}
