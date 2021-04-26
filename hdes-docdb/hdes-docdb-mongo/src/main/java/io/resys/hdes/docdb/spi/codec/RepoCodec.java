package io.resys.hdes.docdb.spi.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import io.resys.hdes.docdb.api.models.ImmutableRepo;
import io.resys.hdes.docdb.api.models.Repo;


public class RepoCodec implements Codec<Repo> {
  
  public static final String ID = "_id";
  public static final String NAME = "name";
  public static final String REV = "rev";
  public static final String PREFIX = "prefix";
  
  @Override
  public void encode(BsonWriter writer, Repo command, EncoderContext encoderContext) {
    writer.writeStartDocument();
    writer.writeString(ID, command.getId());
    writer.writeString(REV, command.getRev());
    writer.writeString(PREFIX, command.getPrefix());
    writer.writeString(NAME, command.getName());
    writer.writeEndDocument();
  }

  @Override
  public Repo decode(BsonReader reader, DecoderContext decoderContext) {
    reader.readStartDocument();
    final var repoId = reader.readString(ID);
    ImmutableRepo.Builder result = ImmutableRepo.builder()
      .id(repoId)
      .rev(reader.readString(REV))
      .prefix(reader.readString(PREFIX))
      .name(reader.readString(NAME));
    reader.readEndDocument();
    return result.build();
  }

  @Override
  public Class<Repo> getEncoderClass() {
    return Repo.class;
  }
}
