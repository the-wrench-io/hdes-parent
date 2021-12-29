package io.resys.hdes.client.spi.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesStore.StoreEntity;
import io.resys.hdes.client.api.ImmutableStoreEntity;
import io.resys.hdes.client.api.config.ThenaConfig;
import io.resys.thena.docdb.api.models.Objects.Blob;


public class ZoeDeserializer implements ThenaConfig.Deserializer {

  private ObjectMapper objectMapper;
  
  public ZoeDeserializer(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }


  @Override
  public StoreEntity fromString(Blob value) {
    try {
      final ImmutableStoreEntity src = objectMapper.readValue(value.getValue(), ImmutableStoreEntity.class);
      return ImmutableStoreEntity.builder().from(src).hash(value.getId()).build();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage() + System.lineSeparator() + value, e);
    }
  }
}
