package io.resys.hdes.pm.repo.api;

/*-
 * #%L
 * hdes-pm-repo
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

import org.immutables.value.Value;

public class PmRevException extends RuntimeException {
  private static final long serialVersionUID = 208954132433481316L;
  
  private final RevisionConflict conflict;
  
  public PmRevException(RevisionConflict conflict, String message, Throwable cause) {
    super(message, cause);
    this.conflict = conflict;
  }

  public PmRevException(RevisionConflict conflict, String message) {
    super(message);
    this.conflict = conflict;
  }
  
  public RevisionConflict getConflict() {
    return conflict;
  }
    
  @Value.Immutable
  public interface RevisionConflict {
    String getId();
    String getRev();
    String getRevToUpdate();
    RevisionType getType();
  }
  
  public static enum RevisionType {
    PROJECT, USER, ACCESS, GROUP, GROUP_USER
  }
}
