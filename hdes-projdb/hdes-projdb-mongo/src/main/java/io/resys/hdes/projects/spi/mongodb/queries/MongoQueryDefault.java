package io.resys.hdes.projects.spi.mongodb.queries;

import io.resys.hdes.projdb.spi.builders.ObjectsQuery;
import io.resys.hdes.projects.spi.mongodb.MongoWrapper;

public class MongoQueryDefault implements ObjectsQuery {
  private final MongoWrapper mongo;
  
  public MongoQueryDefault(MongoWrapper mongo) {
    super();
    this.mongo = mongo;
  }
  @Override
  public GroupQuery group() {
    return new MongoQueryGroup(mongo);
  }
  @Override
  public UserQuery user() {
    return new MongoQueryUser(mongo);
  }
  @Override
  public GroupUserQuery groupUser() {
    return new MongoQueryGroupUser(mongo);
  }
  @Override
  public AccessQuery access() {
    return new MongoQueryAccess(mongo);
  }
  @Override
  public ProjectQuery project() {
    return new MongoQueryProject(mongo);
  }
}
