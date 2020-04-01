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

import io.resys.hdes.ast.spi.visitors.ExpressionParserConsoleVisitor;

public class ExpressionAstNodeTest {
  @Test
  public void complex() throws IOException {
    parse("x.in('aaa', 'bbb', 'x')");
    parse("x.in(y, x, z)");
    parse("sum(y, x, z)");
    parse("sum(10, 20, 30)");
    parse("sum(10_0000, 0.20, 30)");
    parse("sum(true, false)");
    parse("sum(sum(), sum(sum(10, 20)), x.value, 30, z.value, v.x = 10 ? v.sum(v.sum(10, 20), t) : 100)");
    parse("x.value.children");
    parse("x.value.children().value");
    parse("5 = 10 and 20 = 30");
    parse("x = 10 or y = 30");
    parse("x = 10 or sum(y, x, z) = --30");
    parse("true = false ? 10 : sum(20)");
    parse("x++");
    parse("++x + (1-20)");
    parse("(x*y/20) + 1");
  }
  @Test
  public void inclusiveStrings() throws IOException {
    parse("sum(10, 20, 40)");    
  }

  @Test
  public void literals() throws IOException {
    parse("-10");
    parse("-10_000");
    parse("-10.5");
    parse("true");
    parse("false");
    parse("'words'");
  }
  
  @Test
  public void conditionalExpression() throws IOException {
    parse("x >= 20 ? 30 : 40 \r\n");
    parse("x > 20 ? 30 : v");
    parse("x <= k ? 30 : 40");
    parse("x < 20 ? 30 : x");
  }

  @Test
  public void equalityExpression() throws IOException {
    parse("x = 20");
    parse("z != 10");
    parse("z != w");
  }

  @Test
  public void conditionalAndOrExpression() throws IOException {
    parse("x > 6 and z < t");
    parse("x = 20 and y = 10 and z = 100");
    parse("x = 20 and y = 10 or c > 10");
  }

  @Test
  public void arithmeticalExpression() throws IOException {
    parse("x+y/z*89*(x+5)");
  }
  
  public void parse(String value) {
    HdesLexer lexer = new HdesLexer(CharStreams.fromString(value));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ExpressionParser parser = new ExpressionParser(tokens);
    parser.addErrorListener(new ErrorListener());
    ParseTree tree = parser.compilationUnit();
    tree.accept(new ExpressionParserConsoleVisitor());
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
