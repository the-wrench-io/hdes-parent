package io.resys.hdes.object.repo.api.exceptions;

import java.util.Collection;

import io.resys.hdes.object.repo.api.ObjectRepository.Changes;
import io.resys.hdes.object.repo.api.ObjectRepository.IsObject;
import io.resys.hdes.object.repo.api.ObjectRepository.Ref;
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

    public String refDoesNotMatch(String parent, String author, Collection<Ref> refs) {
      return new StringBuilder()
          .append("Commit by: ").append(author)
          .append(" that points to: ").append(parent)
          .append(" does not match with on of the refs:")
          .append(" ").append(refs)
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
    
    public String notFound(String name) {
      return new StringBuilder()
          .append("Commit, tag or ref with id: ").append(name)
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
    
    public String refNotFound(String ref) {
      return new StringBuilder()
          .append("Ref: ").append(ref)
          .append(" does not exist!")
          .toString();
    }
    
    public String notInRef(String commit, String ref) {
      return new StringBuilder()
          .append("Commit: ").append(commit)
          .append(" is not in ref: ").append(ref)
          .append("!")
          .toString();
    }
    public String nothingToMerge(String ref) {
      return new StringBuilder()
          .append("ref: ").append(ref)
          .append(" commits can't be merged because there are no changes!")
          .toString();
    }
    public String conflicts(String ref) {
      return new StringBuilder()
          .append("ref: ").append(ref)
          .append(" commits can't be merged because of conflicts, see status command for resolving conflicts!")
          .toString();
    }
    public String conflicts(String ref, Changes changes) {
      return new StringBuilder()
          .append("ref: ").append(ref)
          .append(" changes: ").append(changes.getName()).append(" ").append(changes.getAction())
          .append(" can't be merged")
          .toString();
    }
  }
}
