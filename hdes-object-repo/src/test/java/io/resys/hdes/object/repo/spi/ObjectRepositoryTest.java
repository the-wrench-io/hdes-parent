package io.resys.hdes.object.repo.spi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.spi.file.FileObjectRepository;

public class ObjectRepositoryTest {

  @Test
  public void createRepository() {
    create().commands().commit().add("file 1", "contentxxxx").author("me@me.com").comment("init").build();
  }

  @Test
  public void createRef() {
    ObjectRepository repo = create();
    Commit firstCommit = repo.commands().commit().add("file 1", "contentxxxx").author("me@me.com").comment("first commit").build();
    
    Commit commit = repo.commands().commit()
    .add("file 2", "contentxxxx")
    .add("file 3", "contentxxxx")
    .add("file 4", "contentxxxx1")
    .ref("new-ref-1")
    .author("me@me.com")
    .parent(firstCommit.getId())
    .comment("init second ref").build();
    
    repo.commands().checkout().from("new-ref-1").build();
    
    repo.commands().merge()
    .author("merger@me.com")
    .ref("new-ref-1").build();
  }
  
  private static ObjectRepository create() {
    try {
      File file = Files.createTempDirectory("test-" + System.currentTimeMillis()).toFile();
      
      return FileObjectRepository.config()
          .directory(file)
          .build();
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
