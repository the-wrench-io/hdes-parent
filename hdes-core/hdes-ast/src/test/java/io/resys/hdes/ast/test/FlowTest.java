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
        flow x1 ({}):{} {}
        flow x2 ({ arg1: integer, arg2: integer }):{} {}
        flow x3 ({ arg0: {}}):{} {}
        flow x4 ({ person: { firstName: integer, lastName: integer }}):{} {}
       """);
  }

  @Test
  public void taskWhenThen() throws IOException {
    parse("""      
        flow AgeProductSelection( { age: integer, office: string } ):
                                  { products: { name: string }[], log: string } {
          GetAgeScore() {
            if (age > 80) return NoProductAvailable()
            else continue
          }
          
          SelectProducts() {
            ProductDecisionTable({ inputAge: age })
            
            return { products: $call, log: 'found some products from dt' }
          }
          
          NoProductAvailable() {
            return { products: [], log: 'sorry nothing to sell' }
          }
        }
     """);
  }

  @Test
  public void taskThen() throws IOException {
    parse("""
        flow x({ arg1: integer arg2: integer }): {}
        {
            firstTask() { XXX({ }) return nextTask() }
            nextTask() { return {} } 
        }   
     """);
  }

  @Test
  public void taskManualTask() throws IOException {
    parse("""
       service-task ServiceX({}):{} {
         pkg.name.ClassName {}
       }
       
       flow x({ arg1: integer, arg2: integer }):{} {
         firstTask() { await ServiceX({}) return {} }
       }
     """);
  }

  @Test
  public void taskServiceTask() throws IOException {
    parse("""
       flow x({ arg1: integer, arg2: integer }):{} {
         firstTask() { bestServiceTask({}) return {} } 
       }
     """);
  }

  @Test
  public void taskFlowTaskOverArray() throws IOException {
    parse("""
       flow x({ arg1: integer, arg2: integer, x: integer[] }) : { log: string } {
         firstTask() {
           map(x).to({
              nestedStep() { 
                 bestDT({ input: _ }) 
                 return { out: _bestDTOutput }
              }
           }).as({summary: 'loop completed'}) return { log: _summary }
         }
       }
     """);
  }
  
  @Test
  public void simpleIterationOverArray() throws IOException {
    parse("""
       flow x({ arg1: integer, arg2: integer, x: integer[] }): {} 
       {
           firstTask() {
             map(x).to({ 
               bestDT({ input: _ })
             }) 
             return {}
           }
       }
     """);
  }

  @Test
  public void taskOverDTOutputArray() throws IOException {
    parse("""
       flow x({ arg1: integer, arg2?: integer, x: integer[] }): { code: integer, summary: { value: integer }[] }
       {
         firstTask () { bestDtTask ({}) continue } 
         nextTask () {
           DoSmth ({ value : firstTask.key }) 
           return { code: 5 }
         }
       }
      """);
  }

  @Test
  public void taskDTArray() throws IOException {
    parse("""
       flow x({ arg1: integer, arg2: integer }):{}
       {
         firstTask() { bestDtTask({}) return {} } 
       }
     """);
  }

  @Test
  public void mapping() throws IOException {
    parse("""
       flow x ({ arg1: integer, arg2: integer }): {} {
          firstTask() {
            bestDtTask({ input1: arg1.x1, input2: arg2.x1 })
            return { input1: arg1.x1, input2: arg2.x1 }
          }
        }
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
