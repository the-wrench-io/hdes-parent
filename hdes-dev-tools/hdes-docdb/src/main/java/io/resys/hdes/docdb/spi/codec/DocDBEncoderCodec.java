package io.resys.hdes.docdb.spi.codec;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import io.resys.hdes.docdb.spi.DocDBEncoder;

public class DocDBEncoderCodec implements DocDBEncoder {
  private final CodecRegistry codecRegistry;

  public DocDBEncoderCodec(CodecRegistry codecRegistry) {
    super();
    this.codecRegistry = codecRegistry;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Bson encode(T object) {
    Codec<T> codec = (Codec<T>) codecRegistry.get(object.getClass());
    
    BsonDocument document = new BsonDocument();
    final var writer = new BsonDocumentWriter(document);
    
    codec.encode(writer, object, EncoderContext.builder().build());
    writer.flush();
    writer.close();
    
    return document;
  }
}
