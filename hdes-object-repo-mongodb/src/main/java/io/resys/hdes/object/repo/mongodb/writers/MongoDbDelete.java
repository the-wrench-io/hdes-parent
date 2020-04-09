package io.resys.hdes.object.repo.mongodb.writers;

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
