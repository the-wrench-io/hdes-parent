package io.resys.hdes.object.repo.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.immutables.value.Value;

public interface ObjectRepository {
  public static final String MASTER = "master";
  interface IsObject { String getId(); }
  interface IsName { String getName(); }
  
  Objects objects();
  Commands commands();
  
  interface Commands {
    StatusBuilder status();
    CommitBuilder commit();
    HistoryBuilder history();
    CheckoutBuilder checkout();
    MergeBuilder merge();
    TagBuilder tag();
  }
  
  interface MergeBuilder {
    // Head name from what to merge to "master"
    MergeBuilder head(String name);
    Objects build();
  }
  
  interface StatusBuilder {
    // Build overview of all all heads related to 'master' head
    Status build();
  }
  
  interface TagBuilder {
    // Name of the tag
    TagBuilder name(String name);
    // optional commit for what to add tag
    TagBuilder commit(String commit);
    // tags can be created only from master
    Tag build();
  }
  
  interface HistoryBuilder {
    List<Commit> build();
  }
  
  interface CheckoutBuilder {
    // tag or commit
    CheckoutBuilder from(String name);
    Snapshot build();
  }
  
  interface CommitBuilder {
    CommitBuilder add(String name, String content);
    CommitBuilder delete(String name);
    CommitBuilder change(String name, String content);
    
    CommitBuilder head(String head);
    CommitBuilder parent(String commitId);
    CommitBuilder author(String author);
    CommitBuilder comment(String message);
    Objects build();
  }

  @Value.Immutable
  interface Snapshot {
    String getId();
    Tree getTree();
    List<SnapshotEntry> getValues();
  }
  
  @Value.Immutable
  interface SnapshotEntry {
    String getName();
    String getBlob();
  }
  
  @Value.Immutable
  interface Objects {
    Map<String, Head> getHeads();
    Map<String, Tag> getTags();
    Map<String, IsObject> getValues();
  }
  
  @Value.Immutable
  interface Status {
    List<StatusEntry> getEntries();
  }
  
  @Value.Immutable
  interface StatusEntry {
    String getId();
    String getName();
    String getNewContent();
    String getOldContent();
  }
  
  @Value.Immutable
  interface TreeEntry {
    String getName();
    String getBlob();
  }
  
  @Value.Immutable
  interface Head extends IsName {
    String getCommit();
  }

  @Value.Immutable
  interface Tag extends IsName {
    String getCommit();
  }
  
  @Value.Immutable
  interface Tree extends IsObject {
    Map<String, TreeEntry> getValues();
  }
  
  @Value.Immutable
  interface Commit extends IsObject {
    String getAuthor();
    LocalDateTime getDateTime();
    String getMessage();
    Optional<String> getParent();
    String getTree();
  }
  
  @Value.Immutable
  interface Blob extends IsObject {
    String getValue();
  }
}
