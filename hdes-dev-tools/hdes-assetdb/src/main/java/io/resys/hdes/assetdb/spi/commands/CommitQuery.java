package io.resys.hdes.assetdb.spi.commands;

import io.resys.hdes.assetdb.api.AssetClient.Commit;
import io.resys.hdes.assetdb.api.AssetClient.IsObject;
import io.resys.hdes.assetdb.api.AssetClient.Objects;
import io.resys.hdes.assetdb.api.AssetClient.Ref;
import io.resys.hdes.assetdb.api.exceptions.CommitException;
import io.resys.hdes.assetdb.spi.RepoAssert;

public class CommitQuery {
  
  private final Objects objects;
  
  private String commit;
  private String refName;

  private CommitQuery(Objects objects) {
    super();
    this.objects = objects;
  }

  public CommitQuery commit(String commit) {
    this.commit = commit;
    return this;
  }
  public CommitQuery ref(String refName) {
    this.refName = refName;
    return this;
  }

  public Commit get() {
    
    // latest ref commit
    if(commit == null) {
      RepoAssert.notNull(refName, () -> "ref and commit can't be BOTH null!");
      Ref refObject = objects.getRefs().get(refName);
      if(refObject == null) {
        throw new CommitException(CommitException.builder().refNotFound(refName));
      }
      return (Commit) objects.getValues().get(refObject.getCommit());
    }
    
    // commit based on id
    IsObject object = objects.getValues().get(commit);
    if(object == null) {
      throw new CommitException(CommitException.builder().notFound(commit));
    } else if(!(object instanceof Commit)) {
      throw new CommitException(CommitException.builder().notCommit(object));        
    }
    
    // is commit in specified ref
    Commit result = (Commit) object;
    if(refName != null) {
      Ref refObject = objects.getRefs().get(refName);
      if(!containsCommit(objects, refObject.getCommit(), result.getId())) {
        throw new CommitException(CommitException.builder().notInRef(commit, refName));  
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
