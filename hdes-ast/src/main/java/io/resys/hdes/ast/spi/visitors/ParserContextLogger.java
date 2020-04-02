package io.resys.hdes.ast.spi.visitors;

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
