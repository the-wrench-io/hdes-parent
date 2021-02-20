package io.resys.hdes.resource.editor.quarkus.deployment.test;

/*-
 * #%L
 * hdes-projects-quarkus-deployment
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

import java.util.Arrays;

import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.jsr310.Jsr310CodecProvider;
import org.bson.internal.ProvidersCodecRegistry;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;


//-Djava.util.logging.manager=org.jboss.logmanager.LogManager
public class MongoConnectionTester {

  @org.junit.jupiter.api.Test
  public void responsesDef() {
    CodecRegistry codecRegistry = new ProvidersCodecRegistry(Arrays.asList(
        //new PMCodecProvider(),
        new DocumentCodecProvider(),
        new Jsr310CodecProvider(),
        new ValueCodecProvider()
      ));
      
      MongoClient client = MongoClients.create(
          MongoClientSettings.builder()
          .codecRegistry(codecRegistry)
          .applyConnectionString(new ConnectionString("mongodb://appUser:123456@localhost:27017/PM?authSource=RE"))
          .build());
      
      
      MongoDatabase db = client.getDatabase("RE");
      db.getCollection("projects").find().first();
  }
  
}
