package io.resys.hdes.docdb.spi;

import org.bson.conversions.Bson;

public interface DocDBEncoder {
  <T> Bson encode(T object);
}
