package io.resys.hdes.object.repo.mongodb.writers;

/*-
 * #%L
 * hdes-object-repo-mongodb
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import io.resys.hdes.object.repo.api.ImmutableObjects;
import io.resys.hdes.object.repo.api.ObjectRepository.Blob;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.IsObject;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Ref;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;
import io.resys.hdes.object.repo.api.exceptions.RepoException;
import io.resys.hdes.object.repo.mongodb.MongoCommand;
import io.resys.hdes.object.repo.mongodb.MongoCommand.MongoDbConfig;
import io.resys.hdes.object.repo.mongodb.codecs.BlobCodec;
import io.resys.hdes.object.repo.mongodb.codecs.CommitCodec;
import io.resys.hdes.object.repo.mongodb.codecs.RefCodec;
import io.resys.hdes.object.repo.mongodb.codecs.TagCodec;
import io.resys.hdes.object.repo.mongodb.codecs.TreeCodec;
import io.resys.hdes.object.repo.spi.mapper.ObjectRepositoryMapper.Writer;

public class MongoDbWriter implements Writer<MongoClient> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MongoDbWriter.class);
  private final MongoCommand<Objects> command;
  private final MongoDbConfig mongoDbConfig;
  private final Objects src;
  private final StringBuilder log = new StringBuilder("Writing transaction: ").append(System.lineSeparator());

  public MongoDbWriter(
      Objects src,
      MongoCommand<Objects> command,
      MongoDbConfig mongoDbConfig) {
    super();
    this.command = command;
    this.mongoDbConfig = mongoDbConfig;
    this.src = src;
  }

  @Override
  public Objects build(List<Object> objects) {
    return command.accept((client) -> {
      Map<String, Ref> refs = new HashMap<>(src.getRefs());
      Map<String, Tag> tags = new HashMap<>(src.getTags());
      Map<String, IsObject> values = new HashMap<>(src.getValues());
      for (Object value : objects) {
        if (value instanceof Blob) {
          Blob blob = (Blob) value;
          values.put(blob.getId(), visitBlob(client, blob));
        } else if (value instanceof Commit) {
          Commit commit = (Commit) value;
          values.put(commit.getId(), visitCommit(client, commit));
        } else if (value instanceof Tree) {
          Tree tree = (Tree) value;
          values.put(tree.getId(), visitTree(client, tree));
        } else if (value instanceof Ref) {
          Ref ref = (Ref) value;
          refs.put(ref.getName(), visitRef(client, ref));
        } else if (value instanceof Tag) {
          Tag tag = (Tag) value;
          tags.put(tag.getName(), visitTag(client, tag));
        } else {
          throw new RepoException("Unknown object: " + value);
        }
      }
      LOGGER.debug(log.toString());
      return ImmutableObjects.builder().values(values).refs(refs).tags(tags).build();
    });
  }

  @Override
  public Ref visitRef(MongoClient client, Ref ref) {
    final MongoCollection<Ref> collection = client
        .getDatabase(mongoDbConfig.getDb()).getCollection(mongoDbConfig.getRefs(), Ref.class);
    Bson filter = Filters.eq(RefCodec.ID, ref.getName());
    Ref value = collection.find(filter).first();
    if(value != null) {
      collection.updateOne(filter, Updates.set(RefCodec.COMMIT, ref.getCommit()));
      log.append("  - ").append(ref).append(System.lineSeparator());
    } else {
      collection.insertOne(ref);
      log.append("  - ").append(ref).append(System.lineSeparator());  
    }
    return ref;
  }

  @Override
  public Tag visitTag(MongoClient client, Tag tag) {
    final MongoCollection<Tag> collection = client
        .getDatabase(mongoDbConfig.getDb()).getCollection(mongoDbConfig.getTags(), Tag.class);
    
    Bson filter = Filters.eq(TagCodec.ID, tag.getName());
    Tag value = collection.find(filter).first();
    if (value != null) {
      collection.updateOne(filter, Updates.set(TagCodec.COMMIT, tag.getCommit()));
      log.append("  - update ").append(tag).append(System.lineSeparator());
    } else {
      collection.insertOne(value);
      log.append("  - ").append(tag).append(System.lineSeparator());
    }
    
    return tag;
  }

  @Override
  public Commit visitCommit(MongoClient client, Commit commit) {
    final MongoCollection<Commit> collection = client
        .getDatabase(mongoDbConfig.getDb()).getCollection(mongoDbConfig.getObjects(), Commit.class);
    Bson filter = Filters.eq(CommitCodec.ID, commit.getId());
    Commit value = collection.find(filter).first();
    if (value != null) {
      log.append("  - commit reuse: ").append(commit.getId()).append(System.lineSeparator());
    } else {
      collection.insertOne(commit);
      log.append("  - commit: ").append(commit.getId()).append(System.lineSeparator());
    }
    return commit;
  }

  @Override
  public Blob visitBlob(MongoClient client, Blob blob) {
    final MongoCollection<Blob> collection = client
        .getDatabase(mongoDbConfig.getDb()).getCollection(mongoDbConfig.getObjects(), Blob.class);
    Bson filter = Filters.eq(BlobCodec.ID, blob.getId());
    Blob value = collection.find(filter).first();
    if (value != null) {
      log.append("  - blob reuse: ").append(value.getId()).append(System.lineSeparator());
    } else {
      collection.insertOne(blob);
      log.append("  - blob: ").append(blob.getId()).append(System.lineSeparator());
    }
    return blob;
  }

  @Override
  public Tree visitTree(MongoClient client, Tree tree) {
    final MongoCollection<Tree> collection = client
        .getDatabase(mongoDbConfig.getDb()).getCollection(mongoDbConfig.getObjects(), Tree.class);
    Bson filter = Filters.eq(TreeCodec.ID, tree.getId());
    Tree value = collection.find(filter).first();
    if (value != null) {
      log.append("  - tree reuse: ").append(value.getId()).append(System.lineSeparator());
    } else {
      collection.insertOne(tree);
      log.append("  - tree: ").append(tree.getId()).append(System.lineSeparator());
    }
    return tree;
  }

  @Override
  public Head visitHead(MongoClient to, Head head) {
    // TODO Auto-generated method stub
    return null;
  }
}
