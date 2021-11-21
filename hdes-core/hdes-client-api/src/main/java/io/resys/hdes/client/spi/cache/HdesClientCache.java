package io.resys.hdes.client.spi.cache;

import java.util.Optional;

import io.resys.hdes.client.api.ast.AstBody;
import io.resys.hdes.client.api.ast.AstBody.AstSource;
import io.resys.hdes.client.api.programs.Program;

public interface HdesClientCache {
  Optional<Program> getProgram(AstSource src);
  Optional<AstBody> getAst(AstSource src);
  
  Program setProgram(Program program, AstSource src);
  AstBody setAst(AstBody wrapper, AstSource src);
}
