package io.resys.hdes.object.repo.mongodb.codecs;

/*-
 * #%L
 * hdes-storage-mongodb
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

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import io.resys.hdes.object.repo.api.ObjectRepository.Blob;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Ref;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;
import io.resys.hdes.object.repo.api.ObjectRepository.TreeEntry;

public class ObjectRepositoryCodeProvider implements CodecProvider {

  private final CommitCodec commit = new CommitCodec();
  private final BlobCodec blob = new BlobCodec();
  private final TreeEntryCodec treeEntry = new TreeEntryCodec();
  private final TreeCodec tree = new TreeCodec(treeEntry);
  
  private final TagCodec tag = new TagCodec();
  private final RefCodec ref = new RefCodec();
  
  @SuppressWarnings("unchecked")
  @Override
  public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry)  {
    if(Commit.class.isAssignableFrom(clazz)) {
      return (Codec<T>) commit;
    }
    if(Blob.class.isAssignableFrom(clazz)) {
      return (Codec<T>) blob;
    }
    if(Tree.class.isAssignableFrom(clazz)) {
      return (Codec<T>) tree;
    }
    if(TreeEntry.class.isAssignableFrom(clazz)) {
      return (Codec<T>) treeEntry;
    }
    
    if(Tag.class.isAssignableFrom(clazz)) {
      return (Codec<T>) tag;
    }
    if(Ref.class.isAssignableFrom(clazz)) {
      return (Codec<T>) ref;
    }
    return null;
  }
}
