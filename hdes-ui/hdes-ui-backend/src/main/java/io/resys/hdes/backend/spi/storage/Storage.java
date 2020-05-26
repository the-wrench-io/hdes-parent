package io.resys.hdes.backend.spi.storage;

import java.util.stream.Stream;

import io.resys.hdes.backend.api.HdesUIBackend.Def;
import io.resys.hdes.backend.api.HdesUIBackend.DefType;

public interface Storage {
  
  StorageReader read();
  
  StorageWriter write();
  
  interface StorageReader {
    Stream<Def> build(); 
  }
  
  interface StorageWriter {
    StorageWriter name(String name);
    StorageWriter type(DefType type);
    StorageWriter value(String value);
    Def build();
  }
}
