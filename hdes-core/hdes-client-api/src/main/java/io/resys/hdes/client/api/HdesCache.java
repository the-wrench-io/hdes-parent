package io.resys.hdes.client.api;

import java.io.Serializable;
import java.util.Optional;

import org.immutables.value.Value;

import io.resys.hdes.client.api.ast.AstBody;
import io.resys.hdes.client.api.ast.AstBody.AstSource;
import io.resys.hdes.client.api.programs.Program;

public interface HdesCache {
  Optional<Program> getProgram(AstSource src);
  Optional<AstBody> getAst(AstSource src);
  
  Program setProgram(Program program, AstSource src);
  AstBody setAst(AstBody wrapper, AstSource src);
  
  @Value.Immutable
  interface CacheEntry extends Serializable {
    String getId();
    AstSource getSource();
    AstBody getAst();
    Optional<Program> getProgram();
  }
}
