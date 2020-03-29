package io.resys.hdes.datatype.spi.antlr.errors;

/*-
 * #%L
 * hdes-datatype
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

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ErrorNode;
import io.resys.hdes.datatype.api.ImmutableEmptyNode;
import io.resys.hdes.datatype.api.ImmutableErrorNode;
import io.resys.hdes.datatype.api.ImmutablePosition;
import io.resys.hdes.datatype.api.ImmutablePositionToken;

public class AntlrErrorListener extends BaseErrorListener {

  private final List<ErrorNode> errors = new ArrayList<>(); 
  
  public List<ErrorNode> getErrors() {
    return errors;
  }

  @Override
  public void syntaxError(
      Recognizer<?, ?> recognizer,
      Object offendingSymbol,
      int line,
      int charPositionInLine,
      String msg,
      RecognitionException e) {
    
    this.errors.add(ImmutableErrorNode.builder()
        .target(ImmutableEmptyNode.builder()
            .pos(ImmutablePosition.builder()
                .text(e.getCtx().getText())
                .start(ImmutablePositionToken.builder()
                .line(line)
                .col(charPositionInLine)
                .build()).build())
            .build())
        .message(msg).build());
    
    //System.err.println("line " + line + ":" + charPositionInLine + " " + msg);
  }
}
