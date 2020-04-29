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

import io.resys.hdes.ast.api.nodes.AstNode.ScalarType;
import io.resys.hdes.ast.spi.visitors.ast.HdesParserAstNodeVisitor;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes.TokenIdGenerator;

public class ExpressionAstNodeTest {
  @Test
  public void complex() throws IOException {
    parse("x.in('aaa', 'bbb', 'x')", ScalarType.BOOLEAN);
    parse("x.in(y, x, z)", ScalarType.BOOLEAN);
    parse("sum(y, x, z)", ScalarType.INTEGER);
    parse("sum(10, 20, 30)", ScalarType.INTEGER);
    parse("sum(10_0000, 0.20, 30)", ScalarType.DECIMAL);
    parse("sum(sum(), sum(sum(10, 20)), x.value, 30, z.value, v.x = 10 ? v.sum(v.sum(10, 20), t) : 100)", ScalarType.DECIMAL);
    parse("x.value.children", ScalarType.INTEGER);
    parse("x.value.children().value", ScalarType.INTEGER);
    parse("5 = 10 and 20 = 30", ScalarType.BOOLEAN);
    parse("x = 10 or y = 30", ScalarType.BOOLEAN);
    parse("x = 10 or sum(y, x, z) = --30", ScalarType.BOOLEAN);
    parse("true = false ? 10 : sum(20)", ScalarType.INTEGER);
    parse("x++", ScalarType.INTEGER);
    parse("++x + (1-20)", ScalarType.INTEGER);
    parse("(x*y/20) + 1", ScalarType.DECIMAL);
  }
  @Test
  public void inclusiveStrings() throws IOException {
    parse("sum(10, 20, 40)", ScalarType.INTEGER);    
  }

  @Test
  public void literals() throws IOException {
    parse("-10", ScalarType.INTEGER);
    parse("-10_000", ScalarType.INTEGER);
    parse("-10.5", ScalarType.DECIMAL);
    parse("true", ScalarType.BOOLEAN);
    parse("false", ScalarType.BOOLEAN);
    parse("'words'", ScalarType.STRING);
  }
  
  @Test
  public void conditionalExpression() throws IOException {
    parse("x >= 20 ? 30 : 40 \r\n", ScalarType.INTEGER);
    parse("x > 20 ? 30 : v", ScalarType.INTEGER);
    parse("x <= k ? 30 : 40", ScalarType.INTEGER);
    parse("x < 20 ? 30 : x", ScalarType.INTEGER);
  }

  @Test
  public void equalityExpression() throws IOException {
    parse("x = 20", ScalarType.BOOLEAN);
    parse("z != 10", ScalarType.BOOLEAN);
    parse("z != w", ScalarType.BOOLEAN);
  }

  @Test
  public void conditionalAndOrExpression() throws IOException {
    parse("x > 6 and z < t", ScalarType.BOOLEAN);
    parse("x = 20 and y = 10 and z = 100", ScalarType.BOOLEAN);
    parse("x = 20 and y = 10 or c > 10", ScalarType.BOOLEAN);
  }

  @Test
  public void arithmeticalExpression() throws IOException {
    parse("x+y/z*89*(x+5)", ScalarType.DECIMAL);
  }
  
  public void parse(String value, ScalarType evalType) {
    HdesLexer lexer = new HdesLexer(CharStreams.fromString(value));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    HdesParser parser = new HdesParser(tokens);
    parser.addErrorListener(new ErrorListener());
    ParseTree tree = parser.enBody();
    //tree.accept(new HdesParserConsoleVisitor());
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
