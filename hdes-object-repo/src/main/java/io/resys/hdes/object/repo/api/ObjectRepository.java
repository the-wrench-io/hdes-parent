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
  enum ChangeAction { MODIFIED, CREATED, DELETED, CONFLICT }
  
  Objects objects();
  Commands commands();
  
  interface Commands {
    StatusBuilder status();
    CommitBuilder commit();
    HistoryBuilder history();
    CheckoutBuilder checkout();
    MergeBuilder merge();
    TagBuilder tag();
    PullBuilder pull();
  }
  
  interface PullBuilder {
    Objects build();
  }
  
  interface MergeBuilder {
    // ref name from what to merge to "master"
    MergeBuilder ref(String name);
    MergeBuilder author(String author);
    Objects build();
  }
  
  interface StatusBuilder {

    // optional filter for ref
    StatusBuilder ref(String name);
    
    // Build overview of other reds related to 'master' ref
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
    
    CommitBuilder ref(String name);
    CommitBuilder parent(String commitId);
    CommitBuilder author(String author);
    CommitBuilder comment(String message);
    CommitBuilder merge(String commitId);
    Commit build();
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
    Map<String, Ref> getRefs();
    Map<String, Tag> getTags();
    Map<String, IsObject> getValues();
  }
  
  @Value.Immutable
  interface Status {
    List<RefStatus> getEntries();
  }
  
  @Value.Immutable
  interface RefStatus {
    String getName();
    List<Commit> getCommits();
    List<Changes> getChanges();
  }
  
  @Value.Immutable
  interface Changes {
    String getName();
    ChangeAction getAction();
    Optional<String> getNewValue();
    Optional<String> getOldValue();
  }
  
  
  @Value.Immutable
  interface TreeEntry {
    String getName();
    String getBlob();
  }
  
  @Value.Immutable
  interface Ref extends IsName {
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
    Optional<String> getMerge();
    String getTree();
  }
  
  @Value.Immutable
  interface Blob extends IsObject {
    String getValue();
  }
}
