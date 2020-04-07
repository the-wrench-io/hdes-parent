package io.resys.hdes.object.repo.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.immutables.value.Value;

public interface ObjectRepository {
  
  interface IsObject { String getId(); }
  interface IsName { String getName(); }
  
  Objects objects();
  Commands commands();
  
  interface Commands {
    PullBuilder pull();
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
    StatusBuilder add(String name, String content);
    StatusBuilder delete(String name);
    StatusBuilder change(String name, String content);
    StatusBuilder head(String head);
    StatusBuilder parent(String commitId);
    Status build();
  }
  
  interface PullBuilder {
    ObjectRepository build();
  }
  
  interface TagBuilder {
    TagBuilder name(String name);
    Tag build();
  }
  
  interface HistoryBuilder {
    List<Commit> build();
  }
  
  interface CheckoutBuilder {
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
    Head getHead();
    Tree getTree();
    List<Commit> getCommits();
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
