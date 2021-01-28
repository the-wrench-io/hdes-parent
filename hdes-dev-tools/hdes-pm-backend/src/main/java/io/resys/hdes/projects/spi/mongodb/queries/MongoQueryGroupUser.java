package io.resys.hdes.projects.spi.mongodb.queries;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.resys.hdes.projects.api.PmException.ErrorType;
import io.resys.hdes.projects.api.PmRepository.GroupUser;
import io.resys.hdes.projects.spi.mongodb.codecs.GroupUserCodec;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQuery.GroupUserQuery;
import io.resys.hdes.projects.spi.mongodb.support.MongoWrapper;

public class MongoQueryGroupUser extends MongoQueryTemplate<MongoQuery.GroupUserQuery, GroupUser> implements MongoQuery.GroupUserQuery {

  public MongoQueryGroupUser(MongoWrapper mongo) {
    super(mongo);
  }
  @Override
  public GroupUserQuery user(String userId) {
    filters.add(Filters.eq(GroupUserCodec.USER_ID, userId));
    return this;
  }
  @Override
  public GroupUserQuery group(String groupId) {
    filters.add(Filters.eq(GroupUserCodec.GROUP_ID, groupId));
    return this;
  }
  @Override
  protected MongoCollection<GroupUser> getCollection() {
    MongoCollection<GroupUser> collection = mongo.getDb().getCollection(mongo.getConfig().getGroupUsers(), GroupUser.class);
    return collection;
  }
  @Override
  protected ErrorType getErrorType() {
    return ErrorType.GROUP_USER;
  }
}
