package io.resys.hdes.object.repo.spi.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import io.resys.hdes.object.repo.api.ImmutableObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.Commands;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.CommitBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.HistoryBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.PullBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.SnapshotBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.StatusBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.api.ObjectRepository.TagBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;
import io.resys.hdes.object.repo.spi.GenericObjectRepositoryReader;
import io.resys.hdes.object.repo.spi.ObjectRepositoryReader;
import io.resys.hdes.object.repo.spi.file.util.Assert;
import io.resys.hdes.object.repo.spi.file.util.FileUtils;
import io.resys.hdes.object.repo.spi.file.util.FileUtils.FileSystemConfig;

public class FileObjectRepository implements Commands {
  private final ObjectRepository objectRepository;
  
  public FileObjectRepository(Head head, List<Commit> commits, List<Tag> tags, List<Tree> trees) {
    this.objectRepository = ImmutableObjectRepository.builder()
        .commits(commits)
        .tags(tags)
        .trees(trees)
        .head(head)
        .commands(this)
        .build();
  }

  @Override
  public PullBuilder pull() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public StatusBuilder status() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CommitBuilder commit() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SnapshotBuilder snapshot() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public HistoryBuilder history() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TagBuilder tag() {
    // TODO Auto-generated method stub
    return null;
  }
  
  public ObjectRepository getObjectRepository() {
    return objectRepository;
  }

  public static Config config() {
    return new Config();
  }

  public static class Config {
    
    private File directory;

    public Config directory(File directory) {
      this.directory = directory;
      return this;
    }

    public ObjectRepository build() {
      try {
        Assert.notNull(directory, () -> "directory must be defined!");
        FileSystemConfig fileSystem = FileUtils.createOrGetRepo(directory);
        
        ObjectRepositoryReader visitor = new GenericObjectRepositoryReader();
        Head head = visitor.visitHead(Files.readAllBytes(fileSystem.getHead().toPath()));
        List<Commit> commits = FileUtils.readFiles(fileSystem.getCommits(), (id, content) -> visitor.visitCommit(id, content));
        List<Tag> tags = FileUtils.readFiles(fileSystem.getTags(), (id, content) -> visitor.visitTag(id, content));
        List<Tree> trees = FileUtils.readFiles(fileSystem.getTrees(), (id, content) -> visitor.visitTree(id, content));;
        
        return new FileObjectRepository(head, commits, tags, trees).getObjectRepository();
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }
  }
}
