package io.resys.hdes.projects.spi.mongodb.queries;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.resys.hdes.projects.api.PmException.ErrorType;
import io.resys.hdes.projects.api.PmRepository.Access;
import io.resys.hdes.projects.spi.mongodb.codecs.AccessCodec;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQuery.AccessQuery;
import io.resys.hdes.projects.spi.mongodb.support.MongoWrapper;

public class MongoQueryAccess extends MongoQueryTemplate<MongoQuery.AccessQuery, Access> implements MongoQuery.AccessQuery {

  public MongoQueryAccess(MongoWrapper mongo) {
    super(mongo);
  }
  @Override
  public AccessQuery comment(String comment) {
    filters.add(Filters.eq(AccessCodec.COMMENT, comment));
    return this;
  }
  @Override
  public AccessQuery user(String userId) {
    filters.add(Filters.eq(AccessCodec.USER_ID, userId));
    return this;
  }
  @Override
  public AccessQuery group(String groupId) {
    filters.add(Filters.eq(AccessCodec.GROUP_ID, groupId));
    return this;
  }
  @Override
  public AccessQuery project(String projectId) {
    filters.add(Filters.eq(AccessCodec.PROJECT_ID, projectId));
    return this;
  }
  @Override
  protected MongoCollection<Access> getCollection() {
    MongoCollection<Access> collection = mongo.getDb().getCollection(mongo.getConfig().getAccess(), Access.class);
    return collection;
  }
  @Override
  protected ErrorType getErrorType() {
    return ErrorType.ACCESS;
  }
}
