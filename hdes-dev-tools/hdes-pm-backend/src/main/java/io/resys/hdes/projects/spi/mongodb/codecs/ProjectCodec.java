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

import io.resys.hdes.projects.api.ImmutableProject;
import io.resys.hdes.projects.api.PmRepository.Project;


public class ProjectCodec implements Codec<Project> {
  
  public static final String NAME = "name";
  
  @Override
  public void encode(BsonWriter writer, Project command, EncoderContext encoderContext) {
    writer.writeStartDocument();
    writer.writeString(CodecUtil.ID, command.getId());
    writer.writeString(CodecUtil.REV, command.getRev());
    writer.writeString(CodecUtil.CREATED, command.getCreated().toString());
    writer.writeString(NAME, command.getName());
    writer.writeEndDocument();
  }

  @Override
  public Project decode(BsonReader reader, DecoderContext decoderContext) {
    reader.readStartDocument();
    Project result = ImmutableProject.builder()
      .id(reader.readString(CodecUtil.ID))
      .rev(reader.readString(CodecUtil.REV))
      .created(LocalDateTime.parse(reader.readString(CodecUtil.CREATED)))
      .name(reader.readString(NAME))
      .build();
    reader.readEndDocument();
    return result;
  }

  @Override
  public Class<Project> getEncoderClass() {
    return Project.class;
  }
}
