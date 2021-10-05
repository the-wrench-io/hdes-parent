package io.resys.hdes.client.api.ast;

import java.util.List;

import io.resys.hdes.client.api.ast.AstType.AstCommandType;

public interface AstChangeset {
  int getLine();
  String getValue();
  List<AstCommandType> getCommands();
}
