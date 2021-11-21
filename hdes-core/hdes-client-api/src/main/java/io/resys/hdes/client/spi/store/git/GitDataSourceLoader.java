package io.resys.hdes.client.spi.store.git;

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
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
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
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.ImmutableAstCommand;
import io.resys.hdes.client.spi.staticresources.Sha2;
import io.resys.hdes.client.spi.staticresources.StoreEntityLocation;
import io.resys.hdes.client.spi.store.git.GitConnection.GitEntry;
import io.resys.hdes.client.spi.util.HdesAssert;

public class GitDataSourceLoader implements AutoCloseable {
  private static final Logger LOGGER = LoggerFactory.getLogger(GitDataSourceLoader.class);
  private static final String TAG_PREFIX = "refs/tags/";
  private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
  private final GitConnection conn;
  private final Repository repo;
  private final ObjectId start;
  private final StoreEntityLocation location;

  @Value.Immutable
  public interface GitFileReload {
    String getTreeValue();
  }
  
  @Value.Immutable
  public interface GitFile {
    String getId();
    String getTreeValue();
    String getBlobValue();
    String getBlobHash();
    AstBodyType getBodyType();
  }
  
  public GitDataSourceLoader(GitConnection conn) throws IOException {
    super();
    this.repo = conn.getClient().getRepository();
    this.conn = conn;
    this.start = repo.resolve(Constants.HEAD);
    this.location = conn.getLocation();
    
  }

  public List<GitEntry> read() {
    try {      
      conn.getClient().pull().setTransportConfigCallback(conn.getCallback()).call();
    } catch (GitAPIException e) {
      LOGGER.error("Can't pull repository! " + System.lineSeparator() + e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
    
    final var load = Arrays.asList(
        Map.entry(AstBodyType.DT, this.location.getDtRegex()),
        Map.entry(AstBodyType.FLOW_TASK, this.location.getFlowTaskRegex()),
        Map.entry(AstBodyType.FLOW, this.location.getFlowRegex())
    ).parallelStream().map(this::readFile).collect(Collectors.toList());
    
    
    final var result = new ArrayList<GitEntry>();
    load.stream().forEach(loaded -> loaded.stream().map(this::readEntry).forEach(result::add));
    readTags().forEach(result::add);
    
    return Collections.unmodifiableList(result);
  }
  
  private List<GitEntry> readTags() {
    try {
      final var result = new ArrayList<GitEntry>();
      for(Ref ref : conn.getClient().tagList().call()) {        
        final String name = ref.getName().startsWith(TAG_PREFIX) ? 
            ref.getName().substring(TAG_PREFIX.length()) : 
            ref.getName();
        
        final var commands = Arrays.asList((AstCommand) ImmutableAstCommand.builder().type(AstCommandValue.SET_BODY).value(name).build());
        final var blobValue = conn.getSerializer().write(commands);
        final var id = ref.getObjectId().getName();
        
        RevWalk revWalk = new RevWalk(repo);
        try {
          final RevCommit commit = revWalk.parseCommit(ref.getObjectId());      
          final var created = new Timestamp(commit.getCommitTime() * 1000L);
          final var entry = ImmutableGitEntry.builder()
              .id(id)
              .revision(id)
              .bodyType(AstBodyType.TAG)
              .treeValue(ref.getName())
              .blobValue(blobValue)
              .created(created)
              .modified(created)
              .blobHash(Sha2.blob(blobValue))
              .commands(commands)
              .build();
          result.add(entry);
        } catch (Exception e) {
          LOGGER.error("Can't resolve timestamps for tag: " + name + System.lineSeparator() + e.getMessage(), e);
          throw new RuntimeException(e.getMessage(), e);
        } finally {
          revWalk.close();
        }
      }
      
      return result;
    } catch (GitAPIException e) {
      LOGGER.error("Can't read tags for repository! " + System.lineSeparator() + e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
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
        
        // src/main/resources/assets/flow/06311dd7-b895-4f94-b43d-6903de74fcf5.json
        final var treeValue = conn.getAssetsPath() +  this.location.getFileName(bodyType, id);
        final var gitFile = ImmutableGitFile.builder()
          .id(id)
          .treeValue(treeValue)
          .blobValue(content)
          .bodyType(bodyType)
          .blobHash(Sha2.blob(content))
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
      
      try {
        final var commands = conn.getSerializer().read(entry.getBlobValue());
        final var result = ImmutableGitEntry.builder()
            .id(entry.getId())
            .revision(modTree.getName())
            .bodyType(entry.getBodyType())
            .treeValue(entry.getTreeValue())
            .blobValue(entry.getBlobValue())
            .created(created)
            .modified(modified)
            .blobHash(Sha2.blob(entry.getBlobValue()))
            .commands(commands)
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
      } catch(Exception e) {
        throw new RuntimeException(
            "Failed to create asset from file: '" + entry.getTreeValue() + "'" + System.lineSeparator() +
            "because of: " + e.getMessage() + System.lineSeparator() +
            "with content: " + entry.getBlobValue() 
            , e);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to load timestamps for: " + entry.getTreeValue() + "!" + e.getMessage(), e);
    }
  }
  
  @Override
  public void close() throws Exception {

  }
}