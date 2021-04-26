package io.resys.hdes.projects.spi.mongodb.codecs;

import java.time.LocalDateTime;
import java.util.Optional;

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

import io.resys.hdes.projdb.api.model.ImmutableUser;
import io.resys.hdes.projdb.api.model.Resource.User;
import io.resys.hdes.projdb.api.model.Resource.UserStatus;


public class UserCodec implements Codec<User> {
  
  public static final String EXTERNAL_ID = "externalId";
  public static final String EMAIL = "email";
  public static final String NAME = "name";
  public static final String TOKEN = "token";
  public static final String STATUS = "status";
  
  @Override
  public void encode(BsonWriter writer, User command, EncoderContext encoderContext) {
    writer.writeStartDocument();
    writer.writeString(CodecUtil.ID, command.getId());
    writer.writeString(CodecUtil.REV, command.getRev());
    writer.writeString(CodecUtil.CREATED, command.getCreated().toString());
    writer.writeString(NAME, command.getName());
    writer.writeString(TOKEN, command.getToken());  
    writer.writeString(STATUS, command.getStatus().name());  
    
    if (command.getEmail().isPresent()) {
      writer.writeString(EMAIL, command.getEmail().get());
    } else {
      writer.writeNull(EMAIL);
    }
    
    if (command.getExternalId().isPresent()) {
      writer.writeString(EXTERNAL_ID, command.getExternalId().get());
    } else {
      writer.writeNull(EXTERNAL_ID);
    }
    
    writer.writeEndDocument();
  }

  @Override
  public User decode(BsonReader reader, DecoderContext decoderContext) {
    reader.readStartDocument();
    User result = ImmutableUser.builder()
      .id(reader.readString(CodecUtil.ID))
      .rev(reader.readString(CodecUtil.REV))
      .created(LocalDateTime.parse(reader.readString(CodecUtil.CREATED)))
      .name(reader.readString(NAME))
      .token(reader.readString(TOKEN))
      .status(UserStatus.valueOf(reader.readString(STATUS)))
      .email(Optional.ofNullable(CodecUtil.readNull(EMAIL, reader) ? null : reader.readString()))
      .externalId(Optional.ofNullable(CodecUtil.readNull(EXTERNAL_ID, reader) ? null : reader.readString()))
      .build();
    reader.readEndDocument();
    return result;
  }

  @Override
  public Class<User> getEncoderClass() {
    return User.class;
  }
}
