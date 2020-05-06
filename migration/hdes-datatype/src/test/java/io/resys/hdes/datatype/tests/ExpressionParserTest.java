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

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import io.resys.hdes.datatype.DataTypeLexer;
import io.resys.hdes.datatype.DataTypeParser;

public class ExpressionParserTest {
 
  @Test
  public void inclusiveStrings() throws IOException {
    parse("-10");
    parse("-10_000");
    parse("-10.5");
    parse("x.in('aaa', 'bbb', 'x')");
    parse("x.in(y, x, z)");
    parse("sum(y, x, z)");
    parse("sum(10, 20, 30)");
    parse("sum(10_0000, 0.20, 30)");
    parse("sum(true, false)");
    parse("sum(sum(), sum(sum(10, 20)), x.value, 30, z.value, v.x = 10 ? v.sum(v.sum(10, 20), t) : 100)");
    parse("x.value.children");
    parse("x.value.children().value");
    parse("5 = 10 & 20 = 30");
    parse("x = 10 | y = 30");
    parse("x = 10 | sum(y, x, z) = --30");
    parse("true = false ? 10 : sum(20)");
    parse("x++");
    parse("++x + (1-20)");
    parse("(x*y/20) + 1");
  }
  
  public ParseTree parse(String expression) {
    System.out.println(expression);
    DataTypeLexer lexer = new DataTypeLexer(CharStreams.fromString(expression));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    DataTypeParser parser = new DataTypeParser(tokens);
    parser.addErrorListener(new ErrorListener());
    
    ParseTree tree = parser.compilationUnit();
    
    //tree.accept(new DataTypeJavaVisitor());
    return tree;
  }
  
  public static class ErrorListener extends BaseErrorListener {
    /**
     * Provides a defaulINTEGERt instance of {@link ConsoleErrorListener}.
     */
    public static final ConsoleErrorListener INSTANCE = new ConsoleErrorListener();

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation prints messages to {@link System#err} containing the
     * values of {@code line}, {@code charPositionInLine}, and {@code msg} using
     * the following format.</p>
     *
     * <pre>
     * line <em>line</em>:<em>charPositionInLine</em> <em>msg</em>
     * </pre>
     */
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int charPositionInLine,
                String msg,
                RecognitionException e)
    {
      String error = "line " + line + ":" + charPositionInLine + " " + msg;
      throw new IllegalArgumentException(error);
    }
  }
}
