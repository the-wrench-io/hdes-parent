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

import io.resys.hdes.ast.spi.flow.visitors.FlowParserConsoleVisitor;

public class FlowAstNodeTest {
  @Test
  public void basic() throws IOException {
    parse("id: x description: 'very descriptive value' inputs: {} tasks: {}");
  }

  @Test
  public void inputs() throws IOException {
    parse(
        "id: x\n" +
            "description: 'very descriptive value'\n" +
            "inputs: {\n " +
              "required INTEGER arg1.x1 , \n" +
              "required INTEGER arg2.x1 \n" +
            "}\n" +
            "tasks: {" +
            "}\n");
  }

  @Test
  public void taskWhenThen() throws IOException {
    parse(
        "id: x\n" +
            "description: 'descriptive ' \n" +
            "inputs: {\n "
            + "optional INTEGER arg1.x1,\n"
            + "required INTEGER arg2.x1\n" +
            "}\n" +
            "tasks: {\n" +
            "firstTask: {"
            + "when: 'arg2.x1 > 10' then: nextTask,"
            + "when: 'arg2.x1 < 10' then: nextTask"
            + "} " +
            "}\n");
  }

  @Test
  public void taskThen() throws IOException {
    parse(
        "id: x\n" +
            "description: 'descriptive ' \n" +
            "inputs: {\n "
            + "required INTEGER arg1.x1,\n"
            + "optional INTEGER arg2.x1\n" +
            "}\n" +
            "tasks: {\n" +
            "firstTask: {"
            + "then: nextTask"
            + "} " +
            "}\n");
  }

  @Test
  public void taskManualTask() throws IOException {
    parse(
        "id: x description: 'descriptive '\n" +
            "inputs: {\n"
            + "optional INTEGER arg1.x1,\n"
            + "optional INTEGER arg2.x1\n" +
            "}\n" +
            "tasks: {\n" +
            "firstTask: {\n"
            + "then: nextTask\n"
            + "OBJECT manualTask: bestManualTask mapping: {}\n"
            + "}\n" +
            "}\n");
  }

  @Test
  public void taskServiceTask() throws IOException {
    parse(
        "id: x description: 'descriptive '\n" +
            "inputs: {\n"
            + "optional INTEGER arg1.x1,\n"
            + "optional INTEGER arg2.x1\n" +
            "}\n" +
            "tasks: {\n" +
            "firstTask: {\n"
            + "then: nextTask\n"
            + "OBJECT serviceTask: bestServiceTask mapping: {} \n"
            + "}\n" +
            "}\n");
  }

  @Test
  public void taskFlowTask() throws IOException {
    parse(
        "id: x description: 'descriptive '\n" +
            "inputs: {\n"
            + "optional INTEGER arg1.x1,\n"
            + "optional INTEGER arg2.x1\n" +
            "}\n" +
            "tasks: {\n" +
            "firstTask: {\n"
            + "then: nextTask\n"
            + "OBJECT flowTask: bestFlowTask mapping: {} \n"
            + "}\n" +
            "}\n");
  }

  @Test
  public void taskDT() throws IOException {
    parse(
        "id: x description: 'descriptive '\n" +
            "inputs: {\n"
            + "optional INTEGER arg1.x1,\n"
            + "optional INTEGER arg2.x1\n" +
            "}\n" +
            "tasks: {\n" +
            "firstTask: {\n"
            + "then: nextTask\n"
            + "OBJECT decisionTask: bestDtTask mapping: {} \n"
            + "}\n" +
            "}\n");
  }

  @Test
  public void taskDTArray() throws IOException {
    parse(
        "id: x description: 'descriptive '\n" +
            "inputs: {\n"
            + "optional INTEGER arg1.x1,\n"
            + "optional INTEGER arg2.x1\n" +
            "}\n" +
            "tasks: {\n" +
            "firstTask: {\n"
            + "then: nextTask\n"
            + "ARRAY decisionTask: bestDtTask mapping: {} \n"
            + "  \n"
            + "}\n" +
            "}\n");
  }

  @Test
  public void mapping() throws IOException {
    parse(
        "id: x description: 'descriptive '\n" +
            "inputs: {\n"
            + "optional INTEGER arg1.x1,\n"
            + "optional INTEGER arg2.x1\n" +
            "}\n" +
            "tasks: {\n" +
            
            "firstTask: {\n"
            + "then: endTask\n"
            + "ARRAY decisionTask: bestDtTask \n"
            + "mapping: {\n"
              + "input1: arg1.x1,\n"
              + "input2: arg2.x1\n"
            + "}\n"
          + "},\n" + 

          "endTask: {\n"
          + "END mapping: {\n"
            + "input1: arg1.x1,\n"
            + "input2: arg2.x1\n"
          + "}\n"
        + "}\n"
          
        + "}\n");
  }

  public void parse(String value) {
    HdesLexer lexer = new HdesLexer(CharStreams.fromString(value));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    FlowParser parser = new FlowParser(tokens);
    parser.addErrorListener(new ErrorListener());
    ParseTree tree = parser.flow();
    tree.accept(new FlowParserConsoleVisitor());
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