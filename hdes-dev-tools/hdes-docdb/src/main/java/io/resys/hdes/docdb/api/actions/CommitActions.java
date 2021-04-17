package io.resys.hdes.docdb.api.actions;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.resys.hdes.docdb.api.models.Message;
import io.resys.hdes.docdb.api.models.Objects.Commit;
import io.smallrye.mutiny.Uni;

public interface CommitActions {
  HeadCommitBuilder head();
  MergeBuilder merge();
  RebaseBuilder rebase();
  
  interface RebaseBuilder {
    RebaseBuilder author(String author);
    RebaseBuilder message(String message);
    RebaseBuilder id(String headGid); // head GID to what to append
    RebaseBuilder head(String repoId, @Nullable String headName); // head name, if null main is set
    Uni<CommitResult> build();
  }
  
  interface MergeBuilder {
    MergeBuilder repo(String repoId);
    MergeBuilder from(String fromHeadName);
    MergeBuilder to(String fromHeadName);
    MergeBuilder author(String author);
    MergeBuilder message(String message);
    Uni<CommitResult> build();
  }
  
  interface HeadCommitBuilder {
    HeadCommitBuilder id(String headGid); // head GID to what to append
    HeadCommitBuilder head(String repoId, String headName); // head GID to what to append
    HeadCommitBuilder append(String name, String blob);
    HeadCommitBuilder append(String name, Serializable blob);
    HeadCommitBuilder remove(String name);
    HeadCommitBuilder author(String author);
    HeadCommitBuilder message(String message);
    Uni<CommitResult> build();
  }
  
  enum CommitStatus {
    OK, ERROR, CONFLICT
  }
  
  @Value.Immutable
  interface CommitResult {
    String getGid(); // repo/head
    @Nullable
    Commit getCommit();
    CommitStatus getStatus();
    List<Message> getMessages();
  }
}
