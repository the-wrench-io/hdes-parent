package io.resys.hdes.ast.spi.validators;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.resys.hdes.ast.api.nodes.AstNode.BodyNode;
import io.resys.hdes.ast.api.nodes.AstNode.EmptyBodyNode;
import io.resys.hdes.ast.api.nodes.AstNode.ErrorNode;
import io.resys.hdes.ast.spi.validators.delegates.BodyNodeIdValidator;

public class BodyValidatorVisitor {
  
  
  private static Collection<BodyNodeValidator> DELEGATES = Arrays.asList(
      new BodyNodeIdValidator()
  );
  
  public static void validate(Map<String, BodyNode> body, Map<String, List<ErrorNode>> errors) {
    for(BodyNodeValidator delegate : DELEGATES) {
      
      for(Map.Entry<String, BodyNode> entry : body.entrySet()) {
        if(entry.getValue() instanceof EmptyBodyNode) {
          continue;
        }
        
        // merge into single error list
        List<ErrorNode> newErrors = delegate.validate(entry.getValue());
        List<ErrorNode> oldErrors = errors.get(entry.getKey());
        
        
        List<ErrorNode> merged = new ArrayList<>();
        merged.addAll(newErrors);
        if(oldErrors != null) {
          merged.addAll(oldErrors);
        }
        
        errors.put(entry.getKey(), merged);
      }
    }
  }
}
