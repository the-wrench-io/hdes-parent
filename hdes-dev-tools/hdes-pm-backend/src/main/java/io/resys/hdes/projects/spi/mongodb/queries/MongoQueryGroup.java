package io.resys.hdes.projects.spi.mongodb.queries;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.resys.hdes.projects.api.PmException.ErrorType;
import io.resys.hdes.projects.api.PmRepository.Group;
import io.resys.hdes.projects.spi.mongodb.codecs.GroupCodec;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQuery.GroupQuery;
import io.resys.hdes.projects.spi.mongodb.support.MongoWrapper;

public class MongoQueryGroup extends MongoQueryTemplate<MongoQuery.GroupQuery, Group> implements MongoQuery.GroupQuery {

  public MongoQueryGroup(MongoWrapper mongo) {
    super(mongo);
  }

  @Override
  protected MongoCollection<Group> getCollection() {
    MongoCollection<Group> collection = mongo.getDb().getCollection(mongo.getConfig().getGroups(), Group.class);
    return collection;
  }
  @Override
  protected ErrorType getErrorType() {
    return ErrorType.GROUP;
  }

  @Override
  public GroupQuery name(String name) {
    filters.add(Filters.eq(GroupCodec.NAME, name));
    return this;
  }
}
