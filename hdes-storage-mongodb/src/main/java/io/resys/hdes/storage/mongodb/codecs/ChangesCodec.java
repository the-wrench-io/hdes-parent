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

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.storage.api.Changes;
import io.resys.hdes.storage.api.ImmutableChanges;

public class ChangesCodec implements Codec<Changes> {
  
  public static final String CHANGES_ID = "_id";
  public static final String CHANGES_LABEL = "label";
  public static final String CHANGES_TENANT = "tenant";
  public static final String CHANGES_VALUES = "values";
  
  private final ChangeCodec changeCodec;
  
  public ChangesCodec(ChangeCodec changeCodec) {
    super();
    this.changeCodec = changeCodec;
  }

  @Override
  public void encode(BsonWriter writer, Changes value, EncoderContext encoderContext) {
    writer.writeStartDocument();
    writer.writeString(CHANGES_ID, value.getId());
    writer.writeString(CHANGES_LABEL, value.getLabel());
    writer.writeString(CHANGES_TENANT, value.getTenant());
    writer.writeStartArray(CHANGES_VALUES);
    
    for (DataTypeCommand command : value.getValues()) {
      changeCodec.encode(writer, command, encoderContext);
    }
    
    writer.writeEndArray();
    writer.writeEndDocument();
  }

  @Override
  public Changes decode(BsonReader reader, DecoderContext decoderContext) {
    ImmutableChanges.Builder changes = ImmutableChanges.builder();
    
    reader.readStartDocument();
    changes
        .id(reader.readString(CHANGES_ID))
        .label(reader.readString(CHANGES_LABEL))
        .tenant(reader.readString(CHANGES_TENANT));
    
    reader.readName(CHANGES_VALUES);
    reader.readStartArray();
    List<DataTypeCommand> commands = new ArrayList<>();
    
    while(reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      commands.add(changeCodec.decode(reader, decoderContext));
    }
    reader.readEndArray();
    
    reader.readEndDocument();
    return changes
        .values(commands)
        .build();
  }
  
  @Override
  public Class<Changes> getEncoderClass() {
    return Changes.class;
  }
}
