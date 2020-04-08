package io.resys.hdes.object.repo.spi.commands;

import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.IsObject;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.exceptions.CommitException;
import io.resys.hdes.object.repo.spi.file.RepoAssert;

public class CommitQuery {
  
  private final Objects objects;
  
  private String commit;
  private String head;

  private CommitQuery(Objects objects) {
    super();
    this.objects = objects;
  }

  public CommitQuery commit(String commit) {
    this.commit = commit;
    return this;
  }
  public CommitQuery head(String head) {
    this.head = head;
    return this;
  }

  public Commit get() {
    
    // latest head commit
    if(commit == null) {
      RepoAssert.notNull(head, () -> "head and commit can't be BOTH null!");
      Head headObject = objects.getHeads().get(head);
      if(headObject == null) {
        throw new CommitException(CommitException.builder().headNotFound(head));
      }
      return (Commit) objects.getValues().get(headObject.getCommit());
    }
    
    // commit based on id
    IsObject object = objects.getValues().get(commit);
    if(object == null) {
      throw new CommitException(CommitException.builder().notFound(commit));
    } else if(!(object instanceof Commit)) {
      throw new CommitException(CommitException.builder().notCommit(object));        
    }
    
    // is commit in specified head
    Commit result = (Commit) object;
    if(head != null) {
      Head headObject = objects.getHeads().get(head);
      if(!containsCommit(objects, headObject.getCommit(), result.getId())) {
        throw new CommitException(CommitException.builder().notInHead(commit, head));  
      }
    }
    return result;
  }
  
  private static boolean containsCommit(Objects objects, String fromCommit, String targetCommit) {
    if(fromCommit.equals(targetCommit)) {
      return true;
    }
    Commit commit = (Commit) objects.getValues().get(fromCommit);
    if(commit.getParent().isPresent()) {
      return containsCommit(objects, commit.getParent().get(), targetCommit);  
    }
    return false;
  }
  
  public static CommitQuery builder(Objects objects) {
    return new CommitQuery(objects);
  }
}
