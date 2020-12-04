package io.resys.hdes.ast.test;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.resys.hdes.ast.HdesLexer;
import io.resys.hdes.ast.HdesParser;
import io.resys.hdes.ast.spi.antlr.visitors.HdesParserVisitor;
import io.resys.hdes.ast.spi.antlr.visitors.HdesParserVisitor.ContentNode;

public class DecisionTableTest {
  
  @Test
  public void basic() throws IOException {
    ContentNode node = 
        parse("""
            decision-table mappingDT { accepts {} returns {} map STRING if() to INTEGER {} }
            decision-table matchingFirstDT { accepts {} returns {} match FIRST {} } 
            decision-table matchingFirstALL { accepts {} returns {} match ALL {} }
            """);
    
    assetNode(node, "basic");
  }

  @Test
  public void headers() throws IOException {
    ContentNode node = 
      parse("""
          decision-table basic { 
          accepts { name, lastName: STRING, value?: INTEGER } 
          returns { } 
          map STRING if() to INTEGER {} }
          """);
    assetNode(node, "headers");
  }

  @Test
  public void values() throws IOException {
    ContentNode node = 
    parse("""
        decision-table basic {
        accepts { firstName: STRING lastName: STRING } returns {}
        map STRING if( 'bob', 'sam', 'viv' )
        to INTEGER  {
          firstName {     1,     2,     3 }
          lastName  {     3,    10,    20 }
        } }
        """);
    assetNode(node, "values");
  }

  @Test
  public void matchExpressions() throws IOException {
    ContentNode node = 
    parse("""
        decision-table basic { 
        accepts { name: STRING, lastName: STRING }
        returns { value: INTEGER, exp: INTEGER = value + 20}
        match ALL {
          if ( _ != 'bob'  or _ = 'same' or _ = 'professor',  _ = 'woman' or _ = 'man')   { 4570 }
          if ( _ != 'bob1' or _ = 'same' or _ = 'professor2', _ = 'woman2' or _ = 'man2') { 4590 }
        }}
        """);
    assetNode(node, "matchExpressions");
  }
  
  @Test
  public void equalityExpressions() throws IOException {
    ContentNode node = 
    parse("""
        decision-table basic { 
        accepts { value0: INTEGER, value1: INTEGER }
        returns { value: INTEGER } 
        match ALL {
          if ( _ > 10, _ <= 20 )            { 4570 }
          if ( _ > 10, _ <= 20 and _ > 10 ) { 4570 }
          if ( _ = 6,  _ != 20 and _ > 10 ) { 4570 }
        } }
        """);
    assetNode(node, "equalityExpressions");
  }
  
  
  
  public ContentNode parse(String value) {
    HdesLexer lexer = new HdesLexer(CharStreams.fromString(value));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    HdesParser parser = new HdesParser(tokens);
    parser.addErrorListener(new ErrorListener());
    ParseTree tree = parser.hdesContent();
    return (ContentNode) tree.accept(new HdesParserVisitor());
  }
  
  public static void assetNode(ContentNode node, String file) {
    String actual = DataFormatTestUtil.yaml(node);
    String expected = DataFormatTestUtil.file("ast/DecisionTableTest_" + file + ".yaml");
    Assertions.assertEquals(expected, actual);
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
