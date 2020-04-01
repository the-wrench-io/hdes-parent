package io.resys.hdes.ast;

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

import java.io.IOException;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import io.resys.hdes.ast.spi.flow.visitors.ManualTaskParserConsoleVisitor;

public class ManualTaskNodeTest {
  @Test
  public void basic() throws IOException {
    parse("id: basic \n"
        + "description: 'very descriptive manual task' \n"
        + "inputs: {} \n"
        + "statements: {} \n"
        + "form: { } \n"); 
  }

  @Test
  public void fields() throws IOException {
    parse("id: basic \n"
        + "description: 'very descriptive manual task' \n"
        + "inputs: {} \n"
        + "statements: {} \n"
        + "form: { fields: { } }"); 
  }
  @Test
  public void nestedGroups() throws IOException {
    parse("id: basic \n"
        + "description: 'very descriptive manual task' \n"
        + "inputs: {} \n"
        + "statements: {} \n"
        + "form: {\n"
        +   "groups: {\n"
        +     "{id: cars fields: {}},\n"
        +     "{id: boats fields: {}},\n"
        +     "{id: soups groups: {}}\n"
        +   "} \n"
        + "} \n"); 
  }
  
  
  public void parse(String value) {
    HdesLexer lexer = new HdesLexer(CharStreams.fromString(value));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ManualTaskParser parser = new ManualTaskParser(tokens);
    parser.addErrorListener(new ErrorListener());
    ParseTree tree = parser.mt();
    tree.accept(new ManualTaskParserConsoleVisitor());
  }

  public static class ErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
        Object offendingSymbol,
        int line,
        int charPositionInLine,
        String msg,
        RecognitionException e) {
      String error = "line " + line + ":" + charPositionInLine + " " + msg;
      throw new IllegalArgumentException(error);
    }
  }
}
