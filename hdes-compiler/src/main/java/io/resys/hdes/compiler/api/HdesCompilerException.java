package io.resys.hdes.compiler.api;

import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.ManualTaskBody;

/*-
 * #%L
 * hdes-compiler
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

public class HdesCompilerException extends RuntimeException {

  private static final long serialVersionUID = -7831610317362075176L;

  public HdesCompilerException() {
    super();
  }

  public HdesCompilerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public HdesCompilerException(String message, Throwable cause) {
    super(message, cause);
  }

  public HdesCompilerException(String message) {
    super(message);
  }

  public HdesCompilerException(Throwable cause) {
    super(cause);
  }
  
  
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    public String unknownFileExtension(String filename) {
      return new StringBuilder()
          .append("Unknown file extension: ").append(filename)
          .append(", supported types are: dt, fl, mt")
          .append("!")
          .toString();
    }
    public String unknownAst(AstNode ast) {
      return new StringBuilder()
          .append("Unknown AST: ").append(ast.getClass())
          .append("  - ").append(ast.getClass()).append(System.lineSeparator())
          .append("  supported types are: ").append(System.lineSeparator())
          .append("    - ").append(DecisionTableBody.class).append(System.lineSeparator())
          .append("    - ").append(FlowBody.class).append(System.lineSeparator())
          .append("    - ").append(ManualTaskBody.class).append(System.lineSeparator())
          .toString();
    }
    public String unknownDTExpressionNode(AstNode ast) {
      return new StringBuilder()
          .append("Unknown DT expression AST: ").append(ast.getClass()).append(System.lineSeparator())
          .append("  - ").append(ast).append("!")
          .toString();
    }
    public String unknownDTExpressionOperation(AstNode ast) {
      return new StringBuilder()
          .append("Unknown DT expression operation AST: ").append(ast.getClass()).append(System.lineSeparator())
          .append("  - ").append(ast).append("!")
          .toString();
    }
  }
}
