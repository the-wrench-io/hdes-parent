package io.resys.hdes.docdb.api.actions;

import java.util.List;

import io.resys.hdes.docdb.api.models.Message;
import io.smallrye.mutiny.Uni;

public interface CommitActions {
  HeadCommitBuilder head();
  MergeBuilder merge();
  RebaseBuilder rebase();
  
  interface RebaseBuilder {
    RebaseBuilder author(String author);
    RebaseBuilder message(String message);
    RebaseBuilder id(String headGid); // head GID to what to append
    RebaseBuilder head(String repoId, String headName); // head GID to what to append
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
    HeadCommitBuilder author(String author);
    HeadCommitBuilder message(String message);
    Uni<CommitResult> build();
  }
  
  enum CommitStatus {
    OK, CONFLICT, MERGED
  }
  
  interface CommitResult {
    String getGid(); // new head GID
    String getNewHead();
    String getRepo();
    CommitStatus getStatus();
    List<Message> getMessages();
  }
}
