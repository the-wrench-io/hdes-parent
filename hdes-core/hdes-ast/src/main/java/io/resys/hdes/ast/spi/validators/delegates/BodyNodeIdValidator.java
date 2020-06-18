package io.resys.hdes.ast.spi.validators.delegates;

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
import java.util.List;

import javax.lang.model.SourceVersion;

import io.resys.hdes.ast.api.nodes.AstNode.BodyNode;
import io.resys.hdes.ast.api.nodes.AstNode.ErrorNode;
import io.resys.hdes.ast.api.nodes.ImmutableErrorNode;
import io.resys.hdes.ast.spi.validators.BodyNodeValidator;

public class BodyNodeIdValidator implements BodyNodeValidator {

  private final String VALID_NAME = "Name must start with capital letter followed by lower/upper letters or numbers.";
  
  @Override
  public List<ErrorNode> validate(BodyNode node) {
    String id = node.getId().getValue();
    List<ErrorNode> result = new ArrayList<>();
    

    if( !Character.isUpperCase(id.charAt(0)) || 
        !SourceVersion.isIdentifier(id)) {
      
      result.add(ImmutableErrorNode.builder()
          .message(new StringBuilder()
              .append("Invalid resource name: '")
              .append(id)
              .append("'. ").append(System.lineSeparator())
              .append(VALID_NAME)
              .toString())
          .target(node.getId())
          .build());
      
    } else if(SourceVersion.isKeyword(id)) {
      
      result.add(ImmutableErrorNode.builder()
          .message(new StringBuilder()
              .append("Invalid resource name: '")
              .append(id)
              .append("'. Name contains reserved keywords.").append(System.lineSeparator())
              .append(VALID_NAME)
              .toString())
          .target(node.getId())
          .build());
    }

    return result;
  }
}
