package io.resys.hdes.backend.spi.mongodb.codecs;

import java.time.LocalDateTime;

/*-
 * #%L
 * hdes-storage-mongodb
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import io.resys.hdes.backend.api.ImmutableAccess;
import io.resys.hdes.backend.api.PmRepository.Access;


public class AccessCodec implements Codec<Access> {
  
  public static final String ID = "_id";
  private static final String REV = "rev";
  private static final String CREATED = "created";
  private static final String NAME = "name";
  private static final String USER_ID = "userId";
  private static final String PROJECT_ID = "projectId";
  private static final String TOKEN = "token";
  
  @Override
  public void encode(BsonWriter writer, Access command, EncoderContext encoderContext) {
    writer.writeStartDocument();
    writer.writeString(ID, command.getId());
    writer.writeString(REV, command.getRev());
    writer.writeString(CREATED, command.getCreated().toString());
    writer.writeString(NAME, command.getName());
    writer.writeString(USER_ID, command.getUserId());
    writer.writeString(PROJECT_ID, command.getProjectId());
    writer.writeString(TOKEN, command.getToken());  
    writer.writeEndDocument();
  }

  @Override
  public Access decode(BsonReader reader, DecoderContext decoderContext) {
    reader.readStartDocument();
    Access result = ImmutableAccess.builder()
      .id(reader.readString(ID))
      .rev(reader.readString(REV))
      .created(LocalDateTime.parse(reader.readString(CREATED)))
      .name(reader.readString(NAME))
      .userId(reader.readString(USER_ID))
      .projectId(reader.readString(PROJECT_ID))
      .token(reader.readString(TOKEN))
      .build();
    reader.readEndDocument();
    return result;
  }

  @Override
  public Class<Access> getEncoderClass() {
    return Access.class;
  }
}
