package io.resys.hdes.object.repo.mongodb.tests;

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
  public void createHead() {
    MongoDbConfig.instance(command -> {
      
    
    ObjectRepository repo = create(command);
    Commit firstCommit = repo.commands().commit().add("file 1", "contentxxxx").author("me@me.com").comment("first commit").build();
    
    Commit commit = repo.commands().commit()
    .add("file 2", "contentxxxx")
    .add("file 3", "contentxxxx")
    .add("file 4", "contentxxxx1")
    .head("new-head-1")
    .author("me@me.com")
    .parent(firstCommit.getId())
    .comment("init second head").build();
    
    repo.commands().merge()
    .author("merger@me.com")
    .head("new-head-1").build();
    
    });
  }
  
  private ObjectRepository create(MongoCommand<Objects> command) {
    return MongoDbObjectRepository.config().command(command).build();
  }
}
