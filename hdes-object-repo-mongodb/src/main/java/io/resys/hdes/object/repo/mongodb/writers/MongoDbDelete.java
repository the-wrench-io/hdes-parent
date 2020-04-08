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
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.HeadStatus;
import io.resys.hdes.object.repo.api.ObjectRepository.IsObject;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.mongodb.MongoCommand;
import io.resys.hdes.object.repo.mongodb.MongoCommand.MongoDbConfig;
import io.resys.hdes.object.repo.mongodb.codecs.HeadCodec;
import io.resys.hdes.object.repo.spi.ObjectRepositoryMapper.Delete;

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
  public Objects build(HeadStatus headStatus) {
    return command.accept((client) -> {
      Map<String, Head> heads = new HashMap<>(src.getHeads());
      Map<String, Tag> tags = new HashMap<>(src.getTags());
      Map<String, IsObject> values = new HashMap<>(src.getValues());
      
      Head head = heads.get(headStatus.getHead());
      visitHead(client, head);
      
      heads.remove(headStatus.getHead());
      
      LOGGER.debug(log.toString());
      
      return ImmutableObjects.builder()
          .values(values)
          .heads(heads)
          .tags(tags)
          .build();
    });
  }

  @Override
  public Head visitHead(MongoClient client, Head head) {
    log.append("  - deleting: ").append(head.getName()).append(" - ").append(head);
    
    final MongoCollection<Head> collection = client
        .getDatabase(mongoDbConfig.getDb()).getCollection(mongoDbConfig.getHeads(), Head.class);
    Bson filter = Filters.eq(HeadCodec.ID, head.getName());
    collection.deleteOne(filter);
    
    return head;
  }
}
