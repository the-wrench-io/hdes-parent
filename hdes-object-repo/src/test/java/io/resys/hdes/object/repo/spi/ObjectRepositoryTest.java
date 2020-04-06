package io.resys.hdes.object.repo.spi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Status;
import io.resys.hdes.object.repo.spi.file.FileObjectRepository;

public class ObjectRepositoryTest {
  private ObjectRepository repo = create();

  @Test
  public void createRepository() {
    Commit commit = repo.commands().commit().build();
    Status status = repo.commands().status().build();
  }

  private ObjectRepository create() {
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
