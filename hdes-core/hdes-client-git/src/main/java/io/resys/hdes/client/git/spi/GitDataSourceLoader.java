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
import org.eclipse.jgit.revwalk.RevCommit;
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
import io.resys.hdes.client.git.spi.GitDataSourceImpl.GitEntry;
import io.resys.hdes.client.spi.staticresources.StoreEntityLocation;

public class GitDataSourceLoader implements AutoCloseable {
  private static final Logger LOGGER = LoggerFactory.getLogger(GitDataSourceLoader.class);
  private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
  
  private final GitConnection conn;
  private final RevWalk revWalk;
  private final RevCommit commit;
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
    final var repo = conn.getClient().getRepository();
    this.conn = conn;
    this.revWalk = new RevWalk(repo);
    this.commit = revWalk.parseCommit(repo.resolve(Constants.HEAD));
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
    try {
      final AstBodyType bodyType = src.getKey();
      final String location = src.getValue();
      
      if(LOGGER.isDebugEnabled()) {
        LOGGER.debug("Loading assets from: " + location + "!");
      }
      final var files = new ArrayList<GitFile>();
      for (final var resource : resolver.getResources(location)) {
        final var content = getContent(resource);
        final var fileName = resource.getFilename();
        final var gitFile = ImmutableGitFile.builder()
          .id(fileName.substring(0, fileName.indexOf(".")))
          .treeValue(this.location.getFileName(bodyType, resource.getFilename()))
          .blobValue(content)
          .bodyType(bodyType)
          .build();
        files.add(gitFile);
      }
      
      return files;
    } catch (Exception e) {
      throw new RuntimeException("Failed to load asset from: " + location + "!" + e.getMessage(), e);
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
    try {
      final TreeFilter treeFilter = AndTreeFilter.create(
          PathFilterGroup.createFromStrings(entry.getTreeValue()), 
          TreeFilter.ANY_DIFF);
      
      revWalk.reset();
      revWalk.markStart(commit);
      revWalk.setTreeFilter(treeFilter);
      revWalk.sort(RevSort.COMMIT_TIME_DESC);
      Timestamp modified = new Timestamp(revWalk.next().getCommitTime() * 1000L);

      revWalk.reset();
      revWalk.markStart(commit);
      revWalk.setTreeFilter(treeFilter);
      revWalk.sort(RevSort.COMMIT_TIME_DESC);
      revWalk.sort(RevSort.REVERSE, true);
      Timestamp created = new Timestamp(revWalk.next().getCommitTime() * 1000L);
            
      return ImmutableGitEntry.builder()
          .id(entry.getId())
          .bodyType(entry.getBodyType())
          .treeValue(entry.getTreeValue())
          .blobValue(entry.getBlobValue())
          .created(created)
          .modified(modified)
          .build();
    } catch (IOException e) {
      throw new RuntimeException("Failed to load timestamps for: " + entry.getTreeValue() + "!" + e.getMessage(), e);
    }
  }

  @Override
  public void close() throws Exception {
    revWalk.close();
  }
}