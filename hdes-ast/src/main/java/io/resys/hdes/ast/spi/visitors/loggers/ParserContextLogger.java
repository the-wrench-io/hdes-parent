package io.resys.hdes.ast.spi.visitors.loggers;

/*-
 * #%L
 * hdes-ast
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

import org.antlr.v4.runtime.ParserRuleContext;

public class ParserContextLogger {
  
  public static final void log(ParserRuleContext context) {
    
    StringBuilder step = new StringBuilder();
    ParserRuleContext parent = context;
    while((parent = parent.getParent()) != null) {
      step.append("  ");
    }
    
    System.out.println(step + "visiting: " + context.getClass().getSimpleName() 
        + ": " + context.getText()
        );
  }
}
