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
import org.junit.jupiter.api.Test;

import io.resys.hdes.ast.HdesLexer;
import io.resys.hdes.ast.HdesParser;
import io.resys.hdes.ast.spi.antlr.visitors.HdesParserVisitor;

public class FlowTest {
  @Test
  public void basic() throws IOException {
    parse("""
        flow x1 { accepts {} returns {} steps {} }
        flow x2 { accepts { arg1 INTEGER, arg2 INTEGER } returns {} steps {} }
        flow x3 { accepts { arg0 OBJECT {} } returns {} steps {} }
        flow x4 { accepts { person OBJECT { firstName INTEGER, lastName INTEGER } } returns {} steps {} }
       """);
  }

  @Test
  public void taskWhenThen() throws IOException {
    parse("""
        flow x {
          accepts { arg1 INTEGER, arg2 INTEGER }
          returns {  }
          steps {
            firstTask { 
              when { arg2 > 10} then continue 
              then continue } as { classifier: 10 }
            nextTask {then end-as {} }
          }
        }
     """);
  }

  @Test
  public void taskThen() throws IOException {
    parse("""
        flow x {
          accepts { arg1 INTEGER, arg2 INTEGER }
          returns {  }
          steps {
            firstTask { call XXX { } then nextTask }
            nextTask { then end-as {} } 
          }
        }   
     """);
  }

  @Test
  public void taskManualTask() throws IOException {
    parse("""
       service-task ServiceX {
         accepts {} returns {}
         promise {}
         
         pkg.name.ClassName {}
       }
       flow x {
         accepts { arg1 INTEGER, arg2 INTEGER } returns { }
         steps {
           firstTask { await ServiceX {} then end-as {} }
         }
       }
     """);
  }

  @Test
  public void taskServiceTask() throws IOException {
    parse("""
       flow x {
         accepts { arg1 INTEGER, arg2 INTEGER }
         returns {  }
         steps {
           firstTask { call bestServiceTask {} then end-as {} } 
         }
       }
     """);
  }

  @Test
  public void taskFlowTaskOverArray() throws IOException {
    parse("""
       flow x {
         accepts { arg1 INTEGER, arg2 INTEGER, x ARRAY of INTEGER }
         returns {  }
         steps {
           firstTask {
             maps x to {
                nestedStep { 
                   call bestDT { input: _ } 
                   then end-as { out: _bestDTOutput }
                }
             } then end-as {}
           }
         } 
       }
     """);
  }
  
  @Test
  public void simpleIterationOverArray() throws IOException {
    parse("""
       flow x {
         accepts { arg1 INTEGER, arg2 INTEGER, x ARRAY of INTEGER }
         returns {  }
         steps {
           firstTask {
             maps x to {
               call bestDT { input: _ }
             } then end-as {}
           }
         } 
       }
     """);
  }

  @Test
  public void taskOverDTOutputArray() throws IOException {
    parse("""
       flow x {
         accepts { *arg1 INTEGER, *arg2 INTEGER, x ARRAY of INTEGER }
         returns { code INTEGER, summary ARRAY of OBJECT { value INTEGER } }  
         steps {
           firstTask { call bestDtTask {} then nextTask } 
           nextTask {
             call DoSmth { value : firstTask.key } 
             then end-as { code: 5 }
           }
         }
       }
      """);
  }

  @Test
  public void taskDTArray() throws IOException {
    parse("""
       flow x {
         accepts { arg1 INTEGER, arg2 INTEGER }
         returns {  }
         steps {
           firstTask { call bestDtTask {} then end-as {} } 
         }
       }
     """);
  }

  @Test
  public void mapping() throws IOException {
    parse("""
       flow x {
         accepts { arg1 INTEGER, arg2 INTEGER }
         returns {  }
         steps {
           firstTask {
            call bestDtTask { input1: arg1.x1, input2: arg2.x1 }
            then end-as { input1: arg1.x1, input2: arg2.x1 }
        }}}
     """);
  }
  
  

  public void parse(String value) {
    HdesLexer lexer = new HdesLexer(CharStreams.fromString(value));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    HdesParser parser = new HdesParser(tokens);
    parser.addErrorListener(new ErrorListener());
    ParseTree tree = parser.hdesContent();
    //tree.accept(new HdesParserConsoleVisitor());
    tree.accept(new HdesParserVisitor());
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
