package io.resys.hdes.docdb.spi.codec;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import io.resys.hdes.docdb.api.models.ImmutableRepo;
import io.resys.hdes.docdb.api.models.ImmutableRepoHead;
import io.resys.hdes.docdb.api.models.Repo;
import io.resys.hdes.docdb.api.models.Repo.RepoHead;
import io.resys.hdes.docdb.spi.support.Identifiers;


public class RepoCodec implements Codec<Repo> {
  
  public static final String ID = "_id";
  public static final String REV = "rev";
  private static final String PREFIX = "prefix";
  private static final String HEADS = "heads";
  
  @Override
  public void encode(BsonWriter writer, Repo command, EncoderContext encoderContext) {
    writer.writeStartDocument();
    writer.writeString(ID, command.getId());
    writer.writeString(REV, command.getRev());
    writer.writeString(PREFIX, command.getPrefix());
    
    writer.writeStartArray(HEADS);
    for(RepoHead head : command.getHeads()) {
      writer.writeString(head.getName());
    }
    writer.writeEndArray();
    
    writer.writeEndDocument();
  }

  @Override
  public Repo decode(BsonReader reader, DecoderContext decoderContext) {
    reader.readStartDocument();
    final var repoId = reader.readString(ID);
    ImmutableRepo.Builder result = ImmutableRepo.builder()
      .id(repoId)
      .rev(reader.readString(REV))
      .prefix(reader.readString(PREFIX));

    reader.readStartArray();
    while(reader.getCurrentBsonType() == BsonType.STRING) {
      final var name = reader.readString();
      result.addHeads(ImmutableRepoHead.builder()
          .name(name)
          .gid(Identifiers.toRepoHeadGid(repoId, name))
          .build());
    }
    reader.readEndArray();
    
    reader.readEndDocument();
    return result.build();
  }

  @Override
  public Class<Repo> getEncoderClass() {
    return Repo.class;
  }
}
