package io.resys.hdes.client.git.spi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.git.spi.GitConnectionFactory.GitConnection;
import io.resys.hdes.client.git.spi.HdesStoreGit.GitEntry;
import io.resys.hdes.client.spi.staticresources.Sha2;
import io.resys.hdes.client.spi.staticresources.StoreEntityLocation;
import io.resys.hdes.client.spi.util.HdesAssert;

public class GitDataSourceLoader implements AutoCloseable {
  private static final Logger LOGGER = LoggerFactory.getLogger(GitDataSourceLoader.class);
  private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
  
  private final GitConnection conn;
  private final Repository repo;
  private final ObjectId start;
  private final StoreEntityLocation location;
  
  @Value.Immutable
  public interface GitFile {
    String getId();
    String getTreeValue();
    String getBlobValue();
    AstBodyType getBodyType();
  }
  
  public GitDataSourceLoader(GitConnection conn, StoreEntityLocation location) throws IOException {
    super();
    this.repo = conn.getClient().getRepository();
    this.conn = conn;
    this.start = repo.resolve(Constants.HEAD);
    this.location = location;
  }

  public List<GitEntry> read() {
    final var load = Arrays.asList(
        Map.entry(AstBodyType.DT, this.location.getDtRegex()),
        Map.entry(AstBodyType.FLOW_TASK, this.location.getFlowTaskRegex()),
        Map.entry(AstBodyType.FLOW, this.location.getFlowRegex())
    ).parallelStream().map(this::readFile).collect(Collectors.toList());
    
    final var result = new ArrayList<GitEntry>();
    load.stream().forEach(loaded -> loaded.forEach(this::readEntry));
    return Collections.unmodifiableList(result);
  }
  
  
  private List<GitFile> readFile(Map.Entry<AstBodyType, String> src) {
    final AstBodyType bodyType = src.getKey();
    final String location = src.getValue();
    
    if(LOGGER.isDebugEnabled()) {
      LOGGER.debug("Loading assets from: " + location + "!");
    }

    final var files = new ArrayList<GitFile>();
    try {
      for (final var resource : resolver.getResources("file:" + location)) {
        final var content = getContent(resource);
        final var fileName = resource.getFilename();
        final var id = fileName.substring(0, fileName.indexOf("."));
        final var gitFile = ImmutableGitFile.builder()
          .id(id)
          .treeValue(conn.getAssetsPath() +  this.location.getFileName(bodyType, id))
          .blobValue(content)
          .bodyType(bodyType)
          .build();
        files.add(gitFile);
        
        HdesAssert.isTrue(resource.getFile().getAbsolutePath().endsWith(gitFile.getTreeValue()), () -> "Failed to create correct treeValue for: " + fileName);
      }
      
      return files;
    } catch (Exception e) {
      throw new RuntimeException("Failed to load asset from: '" + location + "'!" + System.lineSeparator() + e.getMessage(), e);
    }
  }
  
  private String getContent(Resource entry) {
    try {
      return IOUtils.toString(entry.getInputStream(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load asset content from: " + entry.getFilename() + "!" + e.getMessage(), e);
    }
  }
  
  private GitEntry readEntry(GitFile entry) {
    try(final var revWalk = new RevWalk(repo)) {
      
      final TreeFilter treeFilter = AndTreeFilter.create(
          PathFilterGroup.createFromStrings(entry.getTreeValue()), 
          TreeFilter.ANY_DIFF);
      
      final var commit = revWalk.parseCommit(start);
      
      revWalk.reset();
      revWalk.markStart(commit);
      revWalk.setTreeFilter(treeFilter);
      revWalk.sort(RevSort.COMMIT_TIME_DESC);
      final var modTree = revWalk.next();
      final var modified = new Timestamp(modTree.getCommitTime() * 1000L);

      revWalk.reset();
      revWalk.markStart(commit);
      revWalk.setTreeFilter(treeFilter);
      revWalk.sort(RevSort.COMMIT_TIME_DESC);
      revWalk.sort(RevSort.REVERSE, true);
      final var created = new Timestamp(revWalk.next().getCommitTime() * 1000L);
      
      final var result = ImmutableGitEntry.builder()
          .id(entry.getId())
          .revision(modTree.getName())
          .bodyType(entry.getBodyType())
          .treeValue(entry.getTreeValue())
          .blobValue(entry.getBlobValue())
          .created(created)
          .modified(modified)
          .blobHash(Sha2.blob(entry.getBlobValue()))
          .build();
      
      if(LOGGER.isDebugEnabled()) {
        final var msg = new StringBuilder()
            .append("Loading path: ").append(result.getTreeValue()).append(System.lineSeparator())
            .append("  - blob murmur3_128: ").append(result.getBlobHash()).append(System.lineSeparator())
            .append("  - body type: ").append(result.getBodyType()).append(System.lineSeparator())
            .append("  - created: ").append(result.getCreated()).append(System.lineSeparator())
            .append("  - modified: ").append(result.getModified()).append(System.lineSeparator())
            .append("  - revision: ").append(result.getRevision()).append(System.lineSeparator());
        LOGGER.debug(msg.toString());
      }
      
      return result;
    } catch (IOException e) {
      throw new RuntimeException("Failed to load timestamps for: " + entry.getTreeValue() + "!" + e.getMessage(), e);
    }
  }

  @Override
  public void close() throws Exception {

  }
}