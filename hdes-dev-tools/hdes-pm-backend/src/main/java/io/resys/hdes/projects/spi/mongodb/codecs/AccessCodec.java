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

import io.resys.hdes.projects.api.ImmutableAccess;
import io.resys.hdes.projects.api.PmRepository.Access;


public class AccessCodec implements Codec<Access> {
  
  public static final String COMMENT = "comment";
  public static final String USER_ID = "userId";
  public static final String GROUP_ID = "groupId";
  public static final String PROJECT_ID = "projectId";
  
  @Override
  public void encode(BsonWriter writer, Access command, EncoderContext encoderContext) {
    writer.writeStartDocument();
    writer.writeString(CodecUtil.ID, command.getId());
    writer.writeString(CodecUtil.REV, command.getRev());
    writer.writeString(CodecUtil.CREATED, command.getCreated().toString());
    writer.writeString(PROJECT_ID, command.getProjectId());
    

    if (command.getComment().isPresent()) {
      writer.writeString(COMMENT, command.getComment().get());
    } else {
      writer.writeNull(COMMENT);
    }
    
    if (command.getUserId().isPresent()) {
      writer.writeString(USER_ID, command.getUserId().get());
    } else {
      writer.writeNull(USER_ID);
    }
    
    if (command.getGroupId().isPresent()) {
      writer.writeString(GROUP_ID, command.getGroupId().get());
    } else {
      writer.writeNull(GROUP_ID);
    }
    writer.writeEndDocument();
  }

  @Override
  public Access decode(BsonReader reader, DecoderContext decoderContext) {
    reader.readStartDocument();
    Access result = ImmutableAccess.builder()
      .id(reader.readString(CodecUtil.ID))
      .rev(reader.readString(CodecUtil.REV))
      .created(LocalDateTime.parse(reader.readString(CodecUtil.CREATED)))
      .projectId(reader.readString(PROJECT_ID))
      .comment(Optional.ofNullable(CodecUtil.readNull(COMMENT, reader) ? null : reader.readString()))
      .userId(Optional.ofNullable(CodecUtil.readNull(USER_ID, reader) ? null : reader.readString()))
      .groupId(Optional.ofNullable(CodecUtil.readNull(GROUP_ID, reader) ? null : reader.readString()))
      .build();
    reader.readEndDocument();
    return result;
  }

  @Override
  public Class<Access> getEncoderClass() {
    return Access.class;
  }
}
