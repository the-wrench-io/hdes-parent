package io.resys.hdes.object.repo.spi.mapper;

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

import java.util.List;

import io.resys.hdes.object.repo.api.ObjectRepository.Blob;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.IsObject;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Ref;
import io.resys.hdes.object.repo.api.ObjectRepository.RefStatus;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;

public interface ObjectRepositoryMapper<T> {

  Serializer serializer();
  Deserializer deserializer();
  Writer<T> writer(Objects from);
  Delete<T> delete(Objects from);
  IdSupplier id();

  interface Delete<T> {
    Ref visitRef(T to, Ref ref);
    Objects build(RefStatus refStatus);
  }
  
  interface Writer<T> {
    Head visitHead(T to, Head head);
    Ref visitRef(T to, Ref ref);
    Tag visitTag(T to, Tag tag);
    Commit visitCommit(T to, Commit commit);
    Blob visitBlob(T to, Blob blob);
    Tree visitTree(T to, Tree tree);
    Objects build(List<Object> objects);
  }
  
  interface Deserializer {
    Head visitHead(String id, byte[] content);
    Ref visitRef(String id, byte[] content);
    Tag visitTag(String id, byte[] content);
    IsObject visitObject(String id, byte[] content);
  }
  interface Serializer {
    byte[] visitHead(Head head);
    byte[] visitRef(Ref ref);
    byte[] visitTag(Tag tag);
    byte[] visitObject(IsObject object);
  }
  
  interface IdSupplier {
    Blob id(Blob blob);
    Tree id(Tree tree);
    Commit id(Commit commit);
  }
}
