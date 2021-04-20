package io.resys.hdes.docdb.api.models;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.resys.hdes.docdb.api.models.Objects.Blob;

@Value.Immutable
public interface Diff {
  enum DiffAction { MODIFIED, CREATED, DELETED, RENAMED }
  enum DiffStatusType { BEHIND, AHEAD, EQUAL }
  
  Repo getRepo();
  String getLeftCommit();
  String getRightCommit();
  DiffStatus getStatus();
  List<DiffEntry> getEntries(); // only if loaded
  
  @Value.Immutable
  interface DiffEntry {
    String getName();
    DiffAction getAction();
    @Nullable
    Blob getLeftValue();
    @Nullable
    Blob getRightRight();
  }
  
  @Value.Immutable
  interface DiffStatus {
    DiffStatusType getLeft();
    DiffStatusType getRight();
    Long getCommits();
  }
}
