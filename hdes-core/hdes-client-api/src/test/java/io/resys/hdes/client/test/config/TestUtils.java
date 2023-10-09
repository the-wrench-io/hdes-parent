package io.resys.hdes.client.test.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.HdesStore.StoreEntity;
import io.resys.hdes.client.api.ImmutableStoreEntity;
import io.resys.hdes.client.api.ast.AstBody;
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.spi.HdesClientImpl;
import io.resys.hdes.client.spi.HdesInMemoryStore;
import io.resys.hdes.client.spi.config.HdesClientConfig.DependencyInjectionContext;
import io.resys.hdes.client.spi.config.HdesClientConfig.ServiceInit;
import io.resys.hdes.client.spi.util.FileUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TestUtils {

  public static LocalDateTime targetDate = LocalDateTime.of(2013, 1, 1, 1, 1);

  public static ObjectMapper objectMapper = new ObjectMapper().registerModules(new JavaTimeModule(), new Jdk8Module(), new GuavaModule());

  public static StoreEntity fileToStoreEntity(String path, AstBody.AstBodyType type) throws JsonProcessingException {
    JsonNode node = TestUtils.objectMapper.readTree(FileUtils.toString(TestUtils.class, path));
    List<AstCommand> commands = new ArrayList<>();
    for (JsonNode child : node.get("body")) {
      commands.add(TestUtils.objectMapper.treeToValue(child, AstCommand.class));
    }
    return ImmutableStoreEntity.builder()
        .id(node.get("id").asText())
        .hash(node.get("hash").asText())
        .bodyType(type)
        .body(commands)
        .build();
  }

  public static HdesClient client = HdesClientImpl.builder()
      .objectMapper(objectMapper)
      .store(new HdesInMemoryStore(new HashMap<>()))
      .dependencyInjectionContext(new DependencyInjectionContext() {
        @Override
        public <T> T get(Class<T> type) {
          return null;
        }
      })
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
