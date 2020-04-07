package io.resys.hdes.object.repo.api.exceptions;

import java.util.Collection;

import io.resys.hdes.object.repo.api.ObjectRepository.Head;
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
  }
}
