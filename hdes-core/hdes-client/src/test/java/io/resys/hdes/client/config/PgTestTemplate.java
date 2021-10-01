package io.resys.hdes.client.config;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/*-
 * #%L
 * thena-docdb-pgsql
 * %%
 * Copyright (C) 2021 Copyright 2021 ReSys OÜ
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

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import io.resys.hdes.client.api.HdesClient;
import io.resys.thena.docdb.api.DocDB;
import io.resys.thena.docdb.api.actions.RepoActions.RepoResult;
import io.resys.thena.docdb.api.models.Repo;
import io.resys.thena.docdb.spi.ClientCollections;
import io.resys.thena.docdb.spi.ClientState;
import io.resys.thena.docdb.spi.DocDBPrettyPrinter;
import io.resys.thena.docdb.spi.pgsql.DocDBFactory;

public class PgTestTemplate {
  private DocDB client;
  @Inject
  io.vertx.mutiny.pgclient.PgPool pgPool;
  
  @BeforeEach
  public void setUp() {
    this.client = DocDBFactory.create()
        .db("junit")
        .client(pgPool)
        .build();
    this.client.repo().create().name("junit").build();
  }
  
  @AfterEach
  public void tearDown() {
  }

  public ClientState createState() {
    final var ctx = ClientCollections.defaults("junit");
    return DocDBFactory.state(ctx, pgPool);
  }
  
  public void printRepo(Repo repo) {
    final String result = new DocDBPrettyPrinter(createState()).print(repo);
    System.out.println(result);
  }
  
  public void prettyPrint(String repoId) {
    Repo repo = getThena().repo().query().id(repoId).get()
        .await().atMost(Duration.ofMinutes(1));
    
    printRepo(repo);
  }

  public String toRepoExport(String repoId) {
    Repo repo = getThena().repo().query().id(repoId).get()
        .await().atMost(Duration.ofMinutes(1));
    final String result = new RepositoryToStaticData(createState()).print(repo);
    return result;
  }

  public DocDB getThena() {
    return client;
  }
  
  public HdesClient getHdes(String repoId) {
    final DocDB client = getThena();    
    RepoResult repo = getThena().repo().create()
        .name(repoId)
        .build()
        .await().atMost(Duration.ofMinutes(1));
    final AtomicInteger gid = new AtomicInteger(0);
    
    throw new RuntimeException("Need to implemented the client");
  }
  
}
