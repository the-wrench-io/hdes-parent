package io.resys.hdes.client.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.spi.HdesClientImpl;
import io.resys.hdes.client.spi.HdesTypeDefsFactory.ServiceInit;

public class TestUtils {

  public static ObjectMapper objectMapper = new ObjectMapper();
  public static HdesClient client = HdesClientImpl.builder()
      .objectMapper(objectMapper)
      .serviceInit(new ServiceInit() {
        @Override
        public <T> T get(Class<T> type) {
          try {
            return type.getDeclaredConstructor().newInstance();
          } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
          }
        }
      })
      .build();

}
