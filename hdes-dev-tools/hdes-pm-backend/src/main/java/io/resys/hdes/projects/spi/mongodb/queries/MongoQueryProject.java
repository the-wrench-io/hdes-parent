package io.resys.hdes.projects.spi.mongodb.queries;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.resys.hdes.projects.api.PmException.ErrorType;
import io.resys.hdes.projects.api.PmRepository.Project;
import io.resys.hdes.projects.spi.mongodb.codecs.ProjectCodec;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQuery.ProjectQuery;
import io.resys.hdes.projects.spi.mongodb.support.MongoWrapper;

public class MongoQueryProject extends MongoQueryTemplate<MongoQuery.ProjectQuery, Project> implements MongoQuery.ProjectQuery {

  public MongoQueryProject(MongoWrapper mongo) {
    super(mongo);
  }

  @Override
  public ProjectQuery name(String name) {
    filters.add(Filters.eq(ProjectCodec.NAME, name));
    return this;
  }
  @Override
  protected MongoCollection<Project> getCollection() {
    MongoCollection<Project> collection = mongo.getDb().getCollection(mongo.getConfig().getProjects(), Project.class);
    return collection;
  }
  @Override
  protected ErrorType getErrorType() {
    return ErrorType.PROJECT;
  }

}
