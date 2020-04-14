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
import java.util.Map;

import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.resys.hdes.object.repo.api.ImmutableObjects;
import io.resys.hdes.object.repo.api.ObjectRepository.IsObject;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Ref;
import io.resys.hdes.object.repo.api.ObjectRepository.RefStatus;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.mongodb.MongoCommand;
import io.resys.hdes.object.repo.mongodb.MongoCommand.MongoDbConfig;
import io.resys.hdes.object.repo.mongodb.codecs.RefCodec;
import io.resys.hdes.object.repo.spi.mapper.ObjectRepositoryMapper.Delete;

public class MongoDbDelete implements Delete<MongoClient> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MongoDbDelete.class);
  private final MongoCommand<Objects> command;
  private final MongoDbConfig mongoDbConfig;
  private final Objects src;
  private final StringBuilder log = new StringBuilder("Writing transaction: ").append(System.lineSeparator());

  public MongoDbDelete(
      Objects src,
      MongoCommand<Objects> command,
      MongoDbConfig mongoDbConfig) {
    super();
    this.command = command;
    this.mongoDbConfig = mongoDbConfig;
    this.src = src;
  }

  @Override
  public Objects build(RefStatus refStatus) {
    return command.accept((client) -> {
      Map<String, Ref> refs = new HashMap<>(src.getRefs());
      Map<String, Tag> tags = new HashMap<>(src.getTags());
      Map<String, IsObject> values = new HashMap<>(src.getValues());
      
      Ref ref = refs.get(refStatus.getName());
      visitRef(client, ref);
      
      refs.remove(refStatus.getName());
      
      LOGGER.debug(log.toString());
      
      return ImmutableObjects.builder()
          .values(values)
          .refs(refs)
          .tags(tags)
          .build();
    });
  }

  @Override
  public Ref visitRef(MongoClient client, Ref ref) {
    log.append("  - deleting: ").append(ref.getName()).append(" - ").append(ref);
    
    final MongoCollection<Ref> collection = client
        .getDatabase(mongoDbConfig.getDb()).getCollection(mongoDbConfig.getRefs(), Ref.class);
    Bson filter = Filters.eq(RefCodec.ID, ref.getName());
    collection.deleteOne(filter);
    
    return ref;
  }
}
