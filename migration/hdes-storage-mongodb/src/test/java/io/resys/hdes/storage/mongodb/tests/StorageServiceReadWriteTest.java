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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.reactivex.Single;
import io.resys.hdes.datatype.api.ImmutableDataTypeCommand;
import io.resys.hdes.storage.api.Changes;
import io.resys.hdes.storage.api.StorageService;
import io.resys.hdes.storage.mongodb.StorageServiceMongoDb;

public class StorageServiceReadWriteTest {
  @Test
  public void writeAndReadCommandTest() {
    client(mongo -> {
      StorageService storageService = StorageServiceMongoDb.config().client(Single.just(mongo)).build();
      
      // create first version
      Changes changes = storageService.changes().save()
          .label("testing resource")
          .changes(Arrays.asList(
              ImmutableDataTypeCommand.builder()
              .type("test type")
              .subType("test sub type")
              .build()))
          .build().blockingGet();
      Assertions.assertNotNull(changes.getId());
      
      // query created version
      Changes persistedChanges = storageService.changes().query().id(changes.getId()).get().blockingFirst();
      TestAssert.assertEquals("StorageServiceReadWriteTest-1.json", persistedChanges);
      
      // append new commands
      persistedChanges = storageService.changes().save()
          .id(persistedChanges.getId())
          .revision(1)
          .changes(Arrays.asList(
              ImmutableDataTypeCommand.builder()
              .id(1)
              .value("something")
              .type("appended test type")
              .subType("appended test sub type")
              .build())).build().blockingGet();
      TestAssert.assertEquals("StorageServiceReadWriteTest-2.json", persistedChanges);
      
    });
  }
}
