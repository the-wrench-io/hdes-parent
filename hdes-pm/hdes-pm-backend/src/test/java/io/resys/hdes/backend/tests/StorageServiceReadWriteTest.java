package io.resys.hdes.backend.tests;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;

/*-
 * #%L
 * hdes-object-repo-mongodb
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

import org.junit.jupiter.api.Test;

import io.resys.hdes.backend.api.PmRepository;
import io.resys.hdes.backend.api.PmRepository.Project;
import io.resys.hdes.backend.spi.mongodb.MongoPmRepository;

public class StorageServiceReadWriteTest {

  @Test
  public void createProject() {
    MongoDbFactory.instance(transaction -> {
      
      PmRepository repo = MongoPmRepository.builder().transaction(transaction).build();
      Project project = repo.projects().create().name("scoring project 1").build();
      Optional<Project> persistedProject = repo.projects().query().findByName(project.getName());
      
      Assertions.assertEquals(project.getName(), persistedProject.get().getName());
      
    });
  }
}
