package io.resys.hdes.client.spi.store.git;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.spi.store.git.GitConnection.GitSerializer;

public class GitSerializerImpl implements GitSerializer {

  private final ObjectMapper objectMapper;
  private final TypeReference<List<AstCommand>> ref = new TypeReference<List<AstCommand>>() {};
  
  public GitSerializerImpl(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }

  @Override
  public List<AstCommand> read(String commands) {
    try {
      if(commands.startsWith("{")) {
        final var tree = objectMapper.readTree(commands);
        return objectMapper.convertValue(tree.get("commands"), ref);
      }
      
      return objectMapper.readValue(commands, ref);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public String write(List<AstCommand> commands) {
    try {
      return objectMapper.writeValueAsString(commands);
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  
}