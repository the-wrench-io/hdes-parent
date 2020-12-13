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

import io.resys.hdes.object.repo.api.ImmutableTag;
import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.api.ObjectRepository.TagBuilder;
import io.resys.hdes.object.repo.api.exceptions.RefException;
import io.resys.hdes.object.repo.spi.RepoAssert;

public class GenericTagBuilder implements TagBuilder {

  private final Objects objects;
  private String name;
  private String commit;
  
  public GenericTagBuilder(Objects objects) {
    super();
    this.objects = objects;
  }  
  @Override
  public TagBuilder name(String name) {
    this.name = name;
    return this;
  }
  @Override
  public TagBuilder commit(String commit) {
    this.commit = commit;
    return this;
  }
  @Override
  public Tag build() {
    RepoAssert.notNull(name, () -> "name can't be null!");
    
    if(objects.getTags().containsKey(name)) {
      throw new RefException(RefException.builder().duplicateTag(name));
    }
    
    if(objects.getRefs().containsKey(name)) {
      throw new RefException(RefException.builder().refNameMatch(name));
    }
    
    // tags can be created only from master
    Commit commit = CommitQuery.builder(objects).commit(this.commit).ref(ObjectRepository.MASTER).get();
    return ImmutableTag.builder().name(name).commit(commit.getId()).build();
  }  
}
