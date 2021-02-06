package io.resys.hdes.projects.spi.mongodb.queries;

/*-
 * #%L
 * hdes-pm-backend
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.resys.hdes.projects.api.ImmutableConstraintViolation;
import io.resys.hdes.projects.api.PmException;
import io.resys.hdes.projects.api.PmException.ConstraintType;
import io.resys.hdes.projects.api.PmException.ErrorType;
import io.resys.hdes.projects.spi.mongodb.codecs.CodecUtil;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQuery.Query;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQuery.QueryResultWithAccess;
import io.resys.hdes.projects.spi.mongodb.support.MongoWrapper;
import io.resys.hdes.projects.spi.support.RepoAssert;

@SuppressWarnings("unchecked")
public abstract class MongoQueryTemplate<Q, T> implements Query<Q, T> {
  protected final List<Bson> filters = new ArrayList<>();
  protected final MongoWrapper mongo;
  protected boolean isAnd = true;
  
  public MongoQueryTemplate(MongoWrapper mongo) {
    super();
    this.mongo = mongo;
  }
  public Q id(Collection<String> id) {
    filters.add(Filters.in(CodecUtil.ID, id));
    return (Q) this;
  }  
  public Q id(String id) {
    filters.add(Filters.eq(CodecUtil.ID, id));
    return (Q) this;
  }
  public Q rev(String rev) {
    filters.add(Filters.eq(CodecUtil.REV, rev));
    return (Q) this;      
  }
  
  protected abstract MongoCollection<T> getCollection();
  protected abstract ErrorType getErrorType();
  
  @Override
  public void delete() {
    RepoAssert.notNull(filters, () -> "there must be at least one filter!");
    getCollection().deleteMany(filters());    
  }
  
  @Override
  public T get() {
    RepoAssert.notNull(filters, () -> "there must be at least one filter!");
    
    T value = getCollection().find(filters()).first();
    if(value == null) {
      String id = getErrorId();
      throw new PmException(ImmutableConstraintViolation.builder()
          .id(id)
          .rev("any")
          .constraint(ConstraintType.NOT_FOUND)
          .type(getErrorType())
          .build(), () -> new StringBuilder()
            .append("entity: '").append(getErrorType().name()).append("' ")
            .append("not found with filters: '").append(id).append("'!")
            .toString()); 
    }
    return value;
  }
  
  @Override
  public Optional<T> findOne() {
    RepoAssert.notNull(filters, () -> "there must be at least one filter!");

    List<T> result = new ArrayList<>();
    MongoCollection<T> collection = getCollection();
      
    if(filters.isEmpty()) {
      collection.find().forEach((Consumer<T>) result::add);
    } else {
      collection
        .find(filters())
        .forEach((Consumer<T>) result::add); 
    }
    
    if(result.size() > 1) {
      String id = getErrorId();
      throw new PmException(ImmutableConstraintViolation.builder()
          .id(id)
          .rev("any")
          .constraint(ConstraintType.NOT_FOUND)
          .type(getErrorType())
          .build(), () -> new StringBuilder()
            .append("entity: '").append(getErrorType().name()).append("' ")
            .append("was found in: '").append(result.size()).append("' instances but expecting [0..1] ")
            .append("with filters: '").append(id).append("'!")
            .toString());
    } else if(result.size() == 1) {
      return Optional.of(result.iterator().next());
    }
    
    return Optional.empty();
  }
  
  @Override
  public Optional<T> findFirst() {
    RepoAssert.notNull(filters, () -> "there must be at least one filter!");

    MongoCollection<T> collection = getCollection();
      
    if(filters.isEmpty()) {
      return Optional.ofNullable(collection.find().first());
    }
    return Optional.ofNullable(collection.find(filters()).first()); 
  }

  @Override
  public List<T> findAll() {
    List<T> result = new ArrayList<>();
    MongoCollection<T> collection = getCollection();
      
    if(filters.isEmpty()) {
      collection.find().forEach((Consumer<T>) result::add);
    } else {
      collection
        .find(filters())
        .forEach((Consumer<T>) result::add); 
    }
    return Collections.unmodifiableList(result);
  }
  
  @Override
  public Q or() {
    isAnd = false;
    return (Q) this;
  }
  
  @Override
  public Bson filters() {
    return isAnd ? Filters.and(filters) : Filters.or(filters);
  }

  @Override
  public QueryResultWithAccess<T> getWithFilter() {
    T value = get();
    ImmutableQueryResultWithAccess.Builder<T> builder = ImmutableQueryResultWithAccess.builder();
    
    return builder
        .filter(filters())
        .value(value)
        .build();
  }
  
  private String getErrorId() {
    return Filters.and(filters()).toString();
  }
}
