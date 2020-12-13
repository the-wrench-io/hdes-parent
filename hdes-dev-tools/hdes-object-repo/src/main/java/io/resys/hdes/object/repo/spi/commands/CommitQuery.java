package io.resys.hdes.object.repo.spi.commands;

/*-
 * #%L
 * hdes-object-repo
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.IsObject;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Ref;
import io.resys.hdes.object.repo.api.exceptions.CommitException;
import io.resys.hdes.object.repo.spi.RepoAssert;

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
