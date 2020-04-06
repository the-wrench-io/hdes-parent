package io.resys.hdes.object.repo.spi.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.object.repo.api.ImmutableObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;
import io.resys.hdes.object.repo.spi.GenericObjectRepositoryReader;
import io.resys.hdes.object.repo.spi.ObjectRepositoryReader;
import io.resys.hdes.object.repo.spi.file.util.Assert;
import io.resys.hdes.object.repo.spi.file.util.FileUtils;

public class FileObjectRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(FileObjectRepository.class);
  private static final String HEAD_PATH = "head";
  private static final String REPO_PATH = "repo";
  private static final String OBJECTS_PATH = "objects";
  private static final String COMMITS_PATH = "commits";
  private static final String TAGS_PATH = "tags";
  private static final String TREES_PATH = "trees";
  
  @Value.Immutable
  public interface FileSystemConfig {
    File getHead();
    File getRepo();
    File getObjects();
    File getCommits();
    File getTrees();
    File getTags();
  }
  @FunctionalInterface
  private interface FileReader<T> {
    T read(String id, byte[] content);
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
        FileSystemConfig fileSystem = createOrGetRepo(directory);
        
        ObjectRepositoryReader visitor = new GenericObjectRepositoryReader();
        Head head = visitor.visitHead(Files.readAllBytes(fileSystem.getHead().toPath()));
        List<Commit> commits = readFiles(fileSystem.getCommits(), (id, content) -> visitor.visitCommit(id, content));
        List<Tag> tags = readFiles(fileSystem.getTags(), (id, content) -> visitor.visitTag(id, content));
        List<Tree> trees = readFiles(fileSystem.getTrees(), (id, content) -> visitor.visitTree(id, content));;
        
        return ImmutableObjectRepository.builder()
            .commits(commits)
            .tags(tags)
            .trees(trees)
            .head(head)
            .commands(new FileCommands())
            .build();
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }

    private static <T> List<T> readFiles(File dir, FileReader<T> consumer) throws IOException {
      List<T> result = new ArrayList<>();
      for (File subDir : dir.listFiles()) {
        for (File resourceFile : subDir.listFiles()) {
          String id = subDir.getName() + resourceFile.getName();
          result.add(consumer.read(id, Files.readAllBytes(resourceFile.toPath())));
        }
      }
      return result;
    }
    
    private static FileSystemConfig createOrGetRepo(File root) throws IOException {
      FileUtils.isWritable(root);
      StringBuilder log = new StringBuilder("Using file based storage. ");
      File repo = new File(root, REPO_PATH);
      if (repo.exists()) {
        log.append("Using existing repo: ");
      } else {
        FileUtils.mkdir(repo);
        log.append("No existing repo, init new: ");
      }
      FileUtils.isWritable(repo);
      
      File head = FileUtils.mkFile(new File(repo, HEAD_PATH));
      File objects = FileUtils.mkdir(new File(repo, OBJECTS_PATH));
      File commits = FileUtils.mkdir(new File(repo, COMMITS_PATH));
      File tags = FileUtils.mkdir(new File(repo, TAGS_PATH));
      File trees = FileUtils.mkdir(new File(repo, TREES_PATH));
      
      log.append(System.lineSeparator())
          .append("  - ").append(repo.getAbsolutePath()).append(System.lineSeparator())
          .append("  - ").append(head.getAbsolutePath()).append(System.lineSeparator())
          .append("  - ").append(trees.getAbsolutePath()).append(System.lineSeparator())
          .append("  - ").append(objects.getAbsolutePath()).append(System.lineSeparator())
          .append("  - ").append(commits.getAbsolutePath()).append(System.lineSeparator())
          .append("  - ").append(tags.getAbsolutePath()).append(System.lineSeparator());
      LOGGER.debug(log.toString());
      return ImmutableFileSystemConfig.builder()
          .head(head)
          .repo(repo)
          .trees(trees)
          .objects(objects)
          .commits(commits)
          .tags(tags)
          .build();
    }

    
   
  }
}
