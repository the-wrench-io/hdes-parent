package io.resys.hdes.object.repo.mongodb.tests;

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

import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.mongodb.MongoCommand;
import io.resys.hdes.object.repo.mongodb.MongoDbObjectRepository;

public class StorageServiceReadWriteTest {
  
  @Test
  public void writeAndReadCommandTest() {
    MongoDbConfig.instance(command -> {
      
      
      MongoDbObjectRepository.config().command(command).build();
      
      
    });
  }
  
  @Test
  public void createRepository() {
    MongoDbConfig.instance(command -> {
      create(command).commands().commit().add("file 1", "contentxxxx").author("me@me.com").comment("init").build();
    });
  }

  @Test
  public void createRef() {
    MongoDbConfig.instance(command -> {
      
    
    ObjectRepository repo = create(command);
    Commit firstCommit = repo.commands().commit().add("file 1", "contentxxxx").author("me@me.com").comment("first commit").build();
    
    Commit commit = repo.commands().commit()
    .add("file 2", "contentxxxx")
    .add("file 3", "contentxxxx")
    .add("file 4", "contentxxxx1")
    .ref("new-ref-1")
    .author("me@me.com")
    .parent(firstCommit.getId())
    .comment("init second ref").build();
    
    repo.commands().merge()
    .author("merger@me.com")
    .ref("new-ref-1").build();
    
    });
  }
  
  private ObjectRepository create(MongoCommand<Objects> command) {
    return MongoDbObjectRepository.config().command(command).build();
  }
}
