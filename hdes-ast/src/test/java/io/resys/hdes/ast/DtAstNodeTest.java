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

import io.resys.hdes.ast.spi.visitors.ast.HdesParserAstNodeVisitor;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes.TokenIdGenerator;

public class DtAstNodeTest {
  @Test
  public void basic() throws IOException {
    parse("define decision-table: basic "
        + "description: 'very descriptive DT' "
        + "headers: {} MATRIX: {}");
    
    parse("define decision-table: basic "
        + "description: 'very descriptive DT' "
        + "headers: {} FIRST: {}");
    
    parse("define decision-table: basic "
        + "description: 'very descriptive DT' "
        + "headers: {} ALL: {}");
  }

  @Test
  public void headers() throws IOException {
    parse("define decision-table: basic \n"
        + "headers: {\n"
        +   "name STRING required IN,\n "
        +   "lastName STRING required IN, \n"
        +   "value INTEGER required OUT\n"
        + "} MATRIX: {\n"
        + "}");
  }

  @Test
  public void values() throws IOException {
    parse("define decision-table: basic \n"
        + "headers: {\n"
        +   "name STRING required IN,\n "
        +   "lastName STRING required IN, \n"
        +   "value INTEGER required OUT\n"
        + "} MATRIX: {\n"
        +   "{ ?, ?, 20 },"
        +   "{ 'bob', 'woman', 4570 }"
        + "}");
  }

  @Test
  public void matchExpressions() throws IOException {
    parse("define decision-table: basic \n"
        + "headers: {\n"
        +   "name STRING required IN,\n "
        +   "lastName STRING required IN, \n"
        +   "value INTEGER required OUT\n"
        + "} ALL: {\n"
        +   "{ not 'bob' or 'same' or 'professor', 'woman' or 'man', 4570 }\n"
        + "}");
  }
  

  @Test
  public void equalityExpressions() throws IOException {
    parse("define decision-table: basic \n"
        + "headers: {\n"
        +   "value0 INTEGER required IN,\n "
        +   "value1 INTEGER required IN, \n"
        +   "value INTEGER required OUT\n"
        + "} ALL: {\n"
        +   "{ > 10, <= 20, 4570 },\n"
        +   "{ > 10, <= 20 and > 10, 4570 },\n"
        +   "{ = 6, != 20 and > 10, 4570 }\n"
        + "}");
  }
  
  
  
  public void parse(String value) {
    HdesLexer lexer = new HdesLexer(CharStreams.fromString(value));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    HdesParser parser = new HdesParser(tokens);
    parser.addErrorListener(new ErrorListener());
    ParseTree tree = parser.hdesBody();
    tree.accept(new HdesParserAstNodeVisitor(new TokenIdGenerator()));
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
