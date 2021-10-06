package io.resys.wrench.assets.bundle.spi.exceptions;

/*-
 * #%L
 * stencil-persistence
 * %%
 * Copyright (C) 2021 Copyright 2021 ReSys OÜ
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

import java.util.List;

import io.resys.thena.docdb.api.actions.CommitActions.CommitResult;
import io.resys.thena.docdb.api.actions.ObjectsActions.BlobObjects;
import io.resys.thena.docdb.api.actions.ObjectsActions.ObjectsResult;
import io.resys.thena.docdb.api.models.Message;

public class DeleteException extends RuntimeException {
  
  private static final long serialVersionUID = -7236435130625341913L;

  private final String id;
  private final List<Message> commit;
  
  public DeleteException(String id, ObjectsResult<BlobObjects> commit) {
    super(msg(id, commit.getMessages()));
    this.id = id;
    this.commit = commit.getMessages();
  }
  
  public DeleteException(String id, CommitResult commit) {
    super(msg(id, commit.getMessages()));
    this.id = id;
    this.commit = commit.getMessages();
  }

  public String getId() {
    return id;
  }
  
  public List<Message> getCommit() {
    return commit;
  }
  
  private static String msg(String entityId, List<Message> commit) {
    StringBuilder messages = new StringBuilder();
    for(var msg : commit) {
      messages
      .append(System.lineSeparator())
      .append("  - ").append(msg.getText());
    }
    
    return new StringBuilder("Can't delete id: ").append(entityId)
        .append(", because of: ").append(messages)
        .toString();
  }
}
