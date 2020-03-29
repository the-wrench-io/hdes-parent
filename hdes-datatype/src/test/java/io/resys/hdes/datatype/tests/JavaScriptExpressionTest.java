package io.resys.hdes.datatype.tests;

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

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import io.resys.hdes.datatype.DataTypeLexer;
import io.resys.hdes.datatype.DataTypeParser;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.CompilationUnit;
import io.resys.hdes.datatype.spi.antlr.syntax.AstNodeVisitor;
import io.resys.hdes.datatype.spi.antlr.syntax.JavaSyntaxVisitor;

public class JavaScriptExpressionTest {
 
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
    parse("x > 6 | z < t");
    parse("x = 20 & y = 10 & z = 100");
    parse("x = 20 & y = 10 | c > 10");
  }

  @Test
  public void arithmeticalExpression() throws IOException {
    parse("x+y/z*89*(x+5)");
  }
  
  public CompilationUnit parse(String expression) {
    DataTypeLexer lexer = new DataTypeLexer(CharStreams.fromString(expression));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    DataTypeParser parser = new DataTypeParser(tokens);
    ParseTree tree = parser.compilationUnit();
    DataTypeExpressionAstNode node = tree.accept(new AstNodeVisitor());
    CompilationUnit result = JavaSyntaxVisitor.from(node);
    
//  run as a script
//  ScriptEngineManager manager = new ScriptEngineManager();
//  ScriptEngine engine = manager.getEngineByName("JavaScript");
    
    System.out.println(result.getValue());
    return result;
  }
}
