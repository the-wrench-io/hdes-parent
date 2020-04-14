package io.resys.hdes.object.repo.spi.file;

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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.resys.hdes.object.repo.api.ImmutableBlob;
import io.resys.hdes.object.repo.api.ImmutableCommit;
import io.resys.hdes.object.repo.api.ImmutableHead;
import io.resys.hdes.object.repo.api.ImmutableRef;
import io.resys.hdes.object.repo.api.ImmutableTag;
import io.resys.hdes.object.repo.api.ImmutableTree;
import io.resys.hdes.object.repo.api.ImmutableTreeEntry;
import io.resys.hdes.object.repo.api.ObjectRepository.Blob;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.IsObject;
import io.resys.hdes.object.repo.api.ObjectRepository.Ref;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;
import io.resys.hdes.object.repo.api.ObjectRepository.TreeEntry;
import io.resys.hdes.object.repo.api.exceptions.RepoException;
import io.resys.hdes.object.repo.spi.mapper.ObjectRepositoryMapper.Deserializer;
import io.resys.hdes.object.repo.spi.mapper.ObjectRepositoryMapper.Serializer;

public class FileObjectsSerializerAndDeserializer implements Serializer, Deserializer {
  public final static FileObjectsSerializerAndDeserializer INSTANCE = new FileObjectsSerializerAndDeserializer();
  
  public static final String TYPE_BLOB = "blob ";
  public static final String TYPE_TREE = "tree ";
  public static final String TYPE_COMMIT = "commit "; 
  
  private FileObjectsSerializerAndDeserializer() {
    super();
  }

  private Commit visitCommit(String id, String content) {
    String[] value = content.split("/n");

    String treeId = value[0].split(" ")[1];
    String[] parent = value[1].split(" ");
    LocalDateTime dateTime = LocalDateTime.parse(value[2].split(" ")[1]);
    String author = value[3].split(" ")[1];
    Optional<String> merge = value.length > 3 ? Optional.of(value[4].split(" ")[1]) : Optional.empty();
    return ImmutableCommit.builder()
        .id(id)
        .parent(parent.length > 1 ? parent[1] : null)
        .tree(treeId)
        .dateTime(dateTime)
        .author(author)
        .merge(merge)
        .build();
  }
  
  private byte[] visitCommit(Commit commit) {
    
    StringBuilder result = new StringBuilder()
    .append(TYPE_COMMIT).append(commit.getTree()).append("/n")
    .append("parent ").append(commit.getParent().orElse("")).append("/n")
    .append("localDateTime ").append(commit.getDateTime()).append("/n")
    .append("author ").append(commit.getAuthor()).append("/n");
    
    if(commit.getMerge().isPresent()) {
      result.append("merge ").append(commit.getMerge().get()).append("/n");
    }
    
    return result.toString().getBytes(StandardCharsets.UTF_8);
  }
  
  private Blob visitBlob(String id, String content) {
    return ImmutableBlob.builder().id(id).value(content.substring(TYPE_BLOB.length())).build();
  }

  private byte[] visitBlob(Blob blob) {
    return new StringBuilder()
        .append(TYPE_BLOB).append(blob.getValue())
        .toString().getBytes(StandardCharsets.UTF_8);
  }
  
  private Tree visitTree(String id, String content) {
    String[] lines = content.split("/n");
    Map<String, TreeEntry> values = new HashMap<>();
    for(int index = 1; index < lines.length; index++) {
      String line = lines[index];
      int sep = line.lastIndexOf(" ");
      
      String name = line.substring(0, sep);
      String value = line.substring(sep);
      
      values.put(name, ImmutableTreeEntry.builder()
          .name(name)
          .blob(value)
          .build());
    }
    return ImmutableTree.builder().id(id).values(values).build();
  }
  
  private byte[] visitTree(Tree tree) {
    StringBuilder result = new StringBuilder()
        .append(TYPE_TREE).append("/n");
    for(TreeEntry entry : tree.getValues().values()) {
      result
      .append(entry.getName()).append(" ")
      .append(entry.getBlob()).append(" ")
      .append("/n");
    }
    return result.toString()
        .toString().getBytes(StandardCharsets.UTF_8);
  }
  
  @Override
  public IsObject visitObject(String id, byte[] content) {
    String value = new String(content, StandardCharsets.UTF_8);
    if(value.startsWith(TYPE_BLOB)) {
      return visitBlob(id, value);
    } else if(value.startsWith(TYPE_TREE)) {
      return visitTree(id, value);
    } else if(value.startsWith(TYPE_COMMIT)) {
      return visitCommit(id, value);
    }
    throw new RepoException("Unknown object: " + id + System.lineSeparator() + value);
  }
  
  @Override
  public Ref visitRef(String id, byte[] content) {
    String commit = new String(content, StandardCharsets.UTF_8);
    return ImmutableRef.builder()
        .name(id)
        .commit(commit)
        .build();
  }

  @Override
  public Tag visitTag(String id, byte[] content) {
    String value = new String(content, StandardCharsets.UTF_8);
    return ImmutableTag.builder()
        .name(id)
        .commit(value)
        .build();
  }
  
  @Override
  public Head visitHead(String id, byte[] content) {
    return ImmutableHead.builder()
        .value(new String(content, StandardCharsets.UTF_8))
        .build();
  }

  @Override
  public byte[] visitRef(Ref ref) {
    return ref.getCommit().getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public byte[] visitTag(Tag tag) {
    return tag.getCommit().getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public byte[] visitHead(Head head) {
    return head.getValue().getBytes(StandardCharsets.UTF_8);
  }
  
  @Override
  public byte[] visitObject(IsObject object) {
    
    if(object instanceof Blob) {
      return visitBlob((Blob) object);
    
    } else if(object instanceof Tree) {
      return visitTree((Tree) object);
    
    } else if(object instanceof Commit) {
      return visitCommit((Commit) object);
    }
    throw new RepoException("Unknown object: " + object);
  }
}
