package io.resys.hdes.backend.spi.storage.local;

/*-
 * #%L
 * hdes-ui-backend
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

import java.io.File;

public class LocalStorageException extends RuntimeException {
  private static final long serialVersionUID = 9163955084870511877L;
  
  public LocalStorageException(String msg) {
    super(msg);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    public String nonExistingLocation(File file) {
      return new StringBuilder()
          .append("Configured location: '").append(file.getAbsolutePath())
          .append("' for .hdes files does not exist!")
          .toString();
    }
    
    public String locationIsNotDirectory(File file) {
      return new StringBuilder()
          .append("Configured location: ").append(file.getAbsolutePath())
          .append(" for .hdes files is not a directory!")
          .toString();
    }
    public String locationCantBeWritten(File file) {
      return new StringBuilder()
          .append("Configured location: ").append(file.getAbsolutePath())
          .append(" for .hdes files has no write permissions!")
          .toString();
    }
    public String fileAlreadyExists(File file) {
      return new StringBuilder()
          .append("Resource already exists: ").append(file.getAbsolutePath()).append("!")
          .toString();
    }
    public String sameResoureInFile(String name, File file) {
      return new StringBuilder()
          .append("Resource with the name '").append(name).append("' already exists in: ").append(file.getAbsolutePath()).append("!")
          .toString();
    }
  }   
}
