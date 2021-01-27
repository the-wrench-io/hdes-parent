package io.resys.hdes.projects.spi.mongodb.codecs;

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

import io.resys.hdes.projects.api.ImmutableGroupUser;
import io.resys.hdes.projects.api.PmRepository.GroupUser;


public class GroupUserCodec implements Codec<GroupUser> {
  
  public static final String ID = "_id";
  public static final String REV = "rev";
  
  private static final String CREATED = "created";
  public static final String USER_ID = "userId";
  public static final String GROUP_ID = "groupId";
  
  @Override
  public void encode(BsonWriter writer, GroupUser command, EncoderContext encoderContext) {
    writer.writeStartDocument();
    writer.writeString(ID, command.getId());
    writer.writeString(REV, command.getRev());
    writer.writeString(CREATED, command.getCreated().toString());
    writer.writeString(USER_ID, command.getUserId());
    writer.writeString(GROUP_ID, command.getGroupId());
    writer.writeEndDocument();
  }

  @Override
  public GroupUser decode(BsonReader reader, DecoderContext decoderContext) {
    reader.readStartDocument();
    GroupUser result = ImmutableGroupUser.builder()
      .id(reader.readString(ID))
      .rev(reader.readString(REV))
      .created(LocalDateTime.parse(reader.readString(CREATED)))
      .userId(reader.readString(USER_ID))
      .groupId(reader.readString(GROUP_ID))
      .build();
    reader.readEndDocument();
    return result;
  }

  @Override
  public Class<GroupUser> getEncoderClass() {
    return GroupUser.class;
  }
}
