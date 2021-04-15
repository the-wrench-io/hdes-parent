package io.resys.hdes.docdb.api.actions;

import java.time.LocalDateTime;

import org.immutables.value.Value;

import io.smallrye.mutiny.Multi;

public interface HistoryActions {

  BlobHistoryBuilder blob();
  
  interface BlobHistoryBuilder {
    BlobHistoryBuilder repo(String repo, String headName);
    BlobHistoryBuilder blobName(String blobName);
    Multi<HistoryResult> build();
  }
  
  @Value.Immutable
  interface HistoryResult {
    String getValue();
    String getCommit();
    LocalDateTime getCreated();
  }
}
