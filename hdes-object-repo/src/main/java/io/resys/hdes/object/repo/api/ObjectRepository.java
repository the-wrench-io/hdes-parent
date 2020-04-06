package io.resys.hdes.object.repo.api;

import java.time.LocalDateTime;
import java.util.List;

import org.immutables.value.Value;

public interface ObjectRepository {
  
  CommitBuilder commit();
  SnapshotBuilder snapshot();
  HistoryBuilder history();
  TagBuilder tags();

  interface TagBuilder {
    List<Tag> build();
  }
  
  interface HistoryBuilder {
    List<Commit> build();
  }
  
  interface SnapshotBuilder {
    SnapshotBuilder from(String commitId);
    Snapshot build();
  }
  
  interface CommitBuilder {
    CommitBuilder add(String name, String content);
    CommitBuilder parent(String commitId);
    CommitBuilder author(String author);
    Commit build();
  }
  
  @Value.Immutable
  interface Snapshot {
    List<SnapshotEntry> getValues();
  }

  @Value.Immutable
  interface SnapshotEntry {
    String getName();
    String getContent();
  }
  
  @Value.Immutable
  interface Head {
    String getCommitId();
  }
  
  @Value.Immutable
  interface Tree {
    String getId();
    List<TreeEntry> getValues();
  }

  @Value.Immutable
  interface TreeEntry {
    String getId();
    String getName();
    String getContentId();
  }
  
  @Value.Immutable
  interface Commit {
    String getId();
    String getAuthor();
    LocalDateTime getDateTime();
    String getParent();
    String getTreeId();
  }

  @Value.Immutable
  interface Tag {
    String getId();
    String getName();
    String getCommitId();
  }
  
  @Value.Immutable
  interface Content {
    String getId();
    byte[] getBytes();
  }
}
