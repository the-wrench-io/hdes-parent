package io.resys.hdes.object.repo.mongodb.codecs;

import java.util.HashMap;
import java.util.Map;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import io.resys.hdes.object.repo.api.ImmutableTree;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;
import io.resys.hdes.object.repo.api.ObjectRepository.TreeEntry;

public class TreeCodec implements Codec<Tree> {
  
  public static final String ID = "_id";
  public static final String VALUES = "values";
  
  private final TreeEntryCodec entryCode;
  
  public TreeCodec(TreeEntryCodec entryCode) {
    super();
    this.entryCode = entryCode;
  }

  @Override
  public void encode(BsonWriter writer, Tree value, EncoderContext encoderContext) {
    writer.writeStartDocument();
    writer.writeString(ID, value.getId());
    writer.writeStartArray(VALUES);
    
    for (TreeEntry command : value.getValues().values()) {
      entryCode.encode(writer, command, encoderContext);
    }
    writer.writeEndArray();
    writer.writeEndDocument();
  }

  @Override
  public Tree decode(BsonReader reader, DecoderContext decoderContext) {
    ImmutableTree.Builder changes = ImmutableTree.builder();
    
    reader.readStartDocument();
    changes.id(reader.readString(ID));
    
    reader.readName(VALUES);
    reader.readStartArray();
    Map<String, TreeEntry> commands = new HashMap<>();
    
    while(reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      TreeEntry entry = entryCode.decode(reader, decoderContext);
      commands.put(entry.getName(), entry);
    }
    reader.readEndArray();
    
    reader.readEndDocument();
    return changes.values(commands).build();
  }
  
  @Override
  public Class<Tree> getEncoderClass() {
    return Tree.class;
  }
}
