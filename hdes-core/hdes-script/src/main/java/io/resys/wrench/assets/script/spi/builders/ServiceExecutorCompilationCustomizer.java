package io.resys.wrench.assets.script.spi.builders;

/*-
 * #%L
 * hdes-script
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import java.lang.reflect.Modifier;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

import io.resys.hdes.client.api.execution.Service.ServiceExecutorType0;
import io.resys.hdes.client.api.execution.Service.ServiceExecutorType1;
import io.resys.hdes.client.api.execution.Service.ServiceExecutorType2;

public class ServiceExecutorCompilationCustomizer extends CompilationCustomizer {

  public ServiceExecutorCompilationCustomizer() {
    super(CompilePhase.CONVERSION);
 }
  
  
  @Override
  public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
    
    
    
    int length = 0;
    ClassNode inputType1 = null;
    ClassNode inputType2 = null;
    ClassNode returnType = null;
    for(org.codehaus.groovy.ast.MethodNode method : classNode.getMethods()) {
      final var isExecute = method.getName().equals("execute") && Modifier.isPublic(method.getModifiers()) && !Modifier.isVolatile(method.getModifiers());
      if(!isExecute) {
        continue;
      }

      length = method.getParameters().length;
      if(length == 0) {
        
      } else if(length == 1) {
        inputType1 = method.getParameters()[0].getType();
      } else if(length == 2) {
        inputType1 = method.getParameters()[0].getType();
        inputType2 = method.getParameters()[1].getType();
      } else {
        return;
      }
      returnType = method.getReturnType();
      break;
    }
    
    if(returnType == null) {
      return;
    }
    
    
    final ClassNode type;
    final GenericsType[] types;
    if(length == 0) {
      type = ClassHelper.make(ServiceExecutorType0.class);
      types = new GenericsType[] {
          new GenericsType(returnType)
      };      
    } else if(length == 1) {
      type = ClassHelper.make(ServiceExecutorType1.class);
      types = new GenericsType[] {
          new GenericsType(inputType1),
          new GenericsType(returnType)
      };
    } else {
      type = ClassHelper.make(ServiceExecutorType2.class);
      types = new GenericsType[] {
          new GenericsType(inputType1),
          new GenericsType(inputType2),
          new GenericsType(returnType)
      };
    }
    //script16332575927621894006461.groovy: 24: A transform used a generics containing ClassNode io.resys.hdes.client.api.execution.Service$ServiceExecutorType0 <Integer> 
    //for the super class io.resys.wrench.assets.bundle.groovy.businesslogic.RuleGroup2 directly. 
    //You are not supposed to do this. Please create a new ClassNode referring to the old ClassNode and use the new ClassNode instead of the old one. 
    //Otherwise the compiler will create wrong descriptors and a potential NullPointerException in TypeResolver in the OpenJDK. 
    //If this is not your own doing, please report this bug to the writer of the transform.
    
    classNode.addInterface(GenericsUtils.makeClassSafeWithGenerics(type, types));
  }

  public static class UnknownInputTypeException extends RuntimeException {
    private static final long serialVersionUID = -5119010536538764035L;

    public UnknownInputTypeException(String message) {
      super(message);
    }
  }
}
