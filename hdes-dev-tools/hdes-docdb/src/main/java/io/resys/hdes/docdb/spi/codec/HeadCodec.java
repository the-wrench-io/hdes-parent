package io.resys.hdes.docdb.spi.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import io.resys.hdes.docdb.api.models.ImmutableHead;
import io.resys.hdes.docdb.api.models.Objects.Head;


public class HeadCodec implements Codec<Head> {
  
  public static final String COMMIT = "rev";
  public static final String NAME = "_id";
  
  @Override
  public void encode(BsonWriter writer, Head command, EncoderContext encoderContext) {
    writer.writeStartDocument();
    writer.writeString(COMMIT, command.getCommit());
    writer.writeString(NAME, command.getName());
    writer.writeEndDocument();
  }

  @Override
  public Head decode(BsonReader reader, DecoderContext decoderContext) {
    reader.readStartDocument();
    ImmutableHead.Builder result = ImmutableHead.builder()
      .commit(reader.readString(COMMIT))
      .name(reader.readString(NAME));
    reader.readEndDocument();
    return result.build();
  }

  @Override
  public Class<Head> getEncoderClass() {
    return Head.class;
  }
}
