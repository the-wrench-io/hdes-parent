package io.resys.wrench.assets.bundle.spi.store.git;

/*-
 * #%L
 * wrench-component-assets-persistence
 * %%
 * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
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

public interface AssetAuthorProvider {

  Author get();

  class Author {
    private final String user;
    private final String email;

    public Author(String user, String email) {
      this.user = user;
      this.email = email;
    }

    public String getUser() {
      return user;
    }

    public String getEmail() {
      return email;
    }
  }
}
