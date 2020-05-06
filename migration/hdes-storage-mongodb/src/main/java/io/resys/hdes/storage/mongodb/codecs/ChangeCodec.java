package io.resys.hdes.storage.mongodb.codecs;

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
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.datatype.api.ImmutableDataTypeCommand;


public class ChangeCodec implements Codec<DataTypeCommand> {
  
  public static final String CHANGE_ID = "_id";
  public static final String CHANGE_TYPE = "type";
  public static final String CHANGE_SUB_TYPE = "subType";
  public static final String CHANGE_VALUE = "value";
  
  @Override
  public void encode(BsonWriter writer, DataTypeCommand command, EncoderContext encoderContext) {
    writer.writeStartDocument();
    writer.writeString(CHANGE_TYPE, command.getType().toString());
    if (command.getId() == null) {
      writer.writeNull(CHANGE_ID);
    } else {
      writer.writeInt32(CHANGE_ID, command.getId());
    }
    if (command.getSubType() == null) {
      writer.writeNull(CHANGE_SUB_TYPE);
    } else {
      writer.writeString(CHANGE_SUB_TYPE, command.getSubType().toString());
    }
    if (command.getValue() == null) {
      writer.writeNull(CHANGE_VALUE);
    } else {
      writer.writeString(CHANGE_VALUE, command.getValue());
    }
    writer.writeEndDocument();
  }

  @Override
  public DataTypeCommand decode(BsonReader reader, DecoderContext decoderContext) {
    reader.readStartDocument();
    DataTypeCommand result = ImmutableDataTypeCommand.builder()
        .type(reader.readString(CHANGE_TYPE))
        .id(isNull(CHANGE_ID, reader) ? null : reader.readInt32())
        .subType(isNull(CHANGE_SUB_TYPE, reader) ? null : reader.readString())
        .value(isNull(CHANGE_VALUE, reader) ? null : reader.readString()).build();
    reader.readEndDocument();
    return result;
  }

  private boolean isNull(String id, BsonReader reader) {
    reader.readName(id);
    if (reader.getCurrentBsonType() == BsonType.NULL) {
      reader.readNull();
      return true;
    }
    return false;
  }

  @Override
  public Class<DataTypeCommand> getEncoderClass() {
    return DataTypeCommand.class;
  }
}
