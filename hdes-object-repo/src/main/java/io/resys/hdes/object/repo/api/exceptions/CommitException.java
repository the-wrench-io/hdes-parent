package io.resys.hdes.object.repo.api.exceptions;

import java.util.Collection;

import io.resys.hdes.object.repo.api.ObjectRepository.Changes;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.IsObject;
import io.resys.hdes.object.repo.api.ObjectRepository.TreeEntry;

public class CommitException extends RepoException {
  private static final long serialVersionUID = -2123781385633987779L;

  public CommitException(String msg) {
    super(msg);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    public String emptyCommit(String parent, String author) {
      return new StringBuilder()
          .append("Commit by: ").append(author)
          .append(" that points to: ").append(parent)
          .append(" is rejected because there are no changes!")
          .toString();
    }

    public String headDoesNotMatch(String parent, String author, Collection<Head> heads) {
      return new StringBuilder()
          .append("Commit by: ").append(author)
          .append(" that points to: ").append(parent)
          .append(" does not match with on of the heads:")
          .append(" ").append(heads)
          .append("!")
          .toString();
    }
    
    public String unknownEntryDeleted(String parent, String author, TreeEntry treeEntry) {
      return new StringBuilder()
          .append("Commit by: ").append(author)
          .append(" that points to: ").append(parent)
          .append(" has unknown entry: ").append(treeEntry)
          .append(" that can't be delete because it does not exist!")
          .toString();
    }
    
    public String lastCommitIsIdentical(String parent, String author) {
      return new StringBuilder()
          .append("Commit by: ").append(author)
          .append(" that points to: ").append(parent)
          .append(" is a duplicate!")
          .toString();
    }
    
    public String unknownParent(String parent, String author) {
      return new StringBuilder()
          .append("Commit by: ").append(author)
          .append(" that points to: ").append(parent)
          .append(" is rejected because parent does not exist!")
          .toString();
    }
    
    public String notFound(String commit) {
      return new StringBuilder()
          .append("Commit: ").append(commit)
          .append(" does not exist!")
          .toString();
    }
    
    public String notCommit(IsObject object) {
      return new StringBuilder()
          .append("Expecting a commit for id: ").append(object.getId())
          .append(" but was: ").append(object.getClass())
          .append("!")
          .toString();
    }
    
    public String headNotFound(String head) {
      return new StringBuilder()
          .append("Head: ").append(head)
          .append(" does not exist!")
          .toString();
    }
    
    public String notInHead(String commit, String head) {
      return new StringBuilder()
          .append("Commit: ").append(commit)
          .append(" is not in head: ").append(head)
          .append("!")
          .toString();
    }
    public String nothingToMerge(String head) {
      return new StringBuilder()
          .append("Head: ").append(head)
          .append(" commits can't be merged because there are no changes!")
          .toString();
    }
    public String conflicts(String head) {
      return new StringBuilder()
          .append("Head: ").append(head)
          .append(" commits can't be merged because of conflicts, see status command for resolving conflicts!")
          .toString();
    }
    public String conflicts(String head, Changes changes) {
      return new StringBuilder()
          .append("Head: ").append(head)
          .append(" changes: ").append(changes.getName()).append(" ").append(changes.getAction())
          .append(" can't be merged")
          .toString();
    }
  }
}
