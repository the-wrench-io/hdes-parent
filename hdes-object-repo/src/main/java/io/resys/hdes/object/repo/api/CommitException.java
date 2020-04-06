package io.resys.hdes.object.repo.api;

import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;

public class CommitException extends RuntimeException {
  private static final long serialVersionUID = 9163955084870511877L;

  private final Head head;
  private final Commit commit;
  
  public CommitException(Head head, Commit commit) {
    super("Can't push commit because it's not pointing to the head!" + System.lineSeparator() + 
        "  head: " + head + System.lineSeparator() + 
        "  commit: " + head);
    this.head = head;
    this.commit = commit;
  }
  
  
  public Head getHead() {
    return head;
  }

  public Commit getCommit() {
    return commit;
  }

}
