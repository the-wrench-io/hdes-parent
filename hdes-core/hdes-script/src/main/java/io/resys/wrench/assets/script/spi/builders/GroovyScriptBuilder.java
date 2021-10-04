package io.resys.wrench.assets.script.spi.builders;

/*-
 * #%L
 * wrench-component-script
 * %%
 * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
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
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.control.CompilerConfiguration;

import com.fasterxml.jackson.databind.JsonNode;

import groovy.lang.GroovyClassLoader;
import io.resys.hdes.client.api.ast.AstType.AstCommandType;
import io.resys.hdes.client.api.ast.AstType.Direction;
import io.resys.hdes.client.api.ast.AstType.ValueType;
import io.resys.hdes.client.api.HdesTypes;
import io.resys.hdes.client.api.HdesTypes.DataTypeBuilder;
import io.resys.hdes.client.api.ast.ServiceAstType;
import io.resys.hdes.client.api.ast.ServiceAstType.ServiceDataParamModel;
import io.resys.hdes.client.api.ast.ServiceAstType.ServiceParamType;
import io.resys.hdes.client.api.execution.Service;
import io.resys.hdes.client.api.execution.ServiceData;
import io.resys.hdes.client.spi.util.Assert;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptBuilder;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptModelBuilder;
import io.resys.wrench.assets.script.spi.ServiceHistoric;
import io.resys.wrench.assets.script.spi.ServiceTemplate;
import io.resys.wrench.assets.script.spi.beans.ImmutableScriptMethodModel;
import io.resys.wrench.assets.script.spi.beans.ImmutableScriptModel;
import io.resys.wrench.assets.script.spi.beans.ImmutableScriptParameterModel;


public class GroovyScriptBuilder implements ScriptBuilder {
  private static final Charset UTF_8 = Charset.forName("utf-8");

  private final HdesTypes dataTypeRepository;
  private final Supplier<ScriptModelBuilder> modelBuilder;
  private final GroovyScriptParser scriptParsers;
  private static final CompilerConfiguration config;
  
  static {
    config = new CompilerConfiguration();
    config.setTargetBytecode(CompilerConfiguration.JDK8);
    config.addCompilationCustomizers(new ServiceExecutorCompilationCustomizer());
  }
  
  private String src;
  private Integer rev;
  private JsonNode jsonNode;

  public GroovyScriptBuilder(
      GroovyScriptParser scriptParsers,
      HdesTypes dataTypeRepository,
      Supplier<ScriptModelBuilder> modelBuilder) {
    super();
    this.dataTypeRepository = dataTypeRepository;
    this.modelBuilder = modelBuilder;
    this.scriptParsers = scriptParsers;
  }

  @Override
  public ScriptBuilder src(String src) {
    this.src = src;
    return this;
  }
  @Override
  public ScriptBuilder src(InputStream src) {
    try {
      this.src = IOUtils.toString(src, UTF_8);
    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    return this;
  }
  @Override
  public ScriptBuilder src(JsonNode jsonNode) {
    this.jsonNode = jsonNode;
    return this;
  }
  @Override
  public ScriptBuilder rev(Integer rev) {
    this.rev = rev;
    return this;
  }
  @Override
  public Service build() {
    Assert.isTrue(src != null || jsonNode != null, () -> "src can't be null!");

    final Map.Entry<String, List<AstCommandType>> src = getSrc(this.src, this.jsonNode);
    final int rev = this.rev != null ? this.rev : src.getValue().size();
    final GroovyClassLoader gcl = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
    
    
    try {
      final Class<?> beanType = generateBeanType(gcl, src.getKey());
      final ImmutableScriptMethodModel method = getMethods(beanType);
      final ServiceAstType model = modelBuilder.get().src(src.getKey()).commands(src.getValue()).rev(rev).type(beanType).method(method).build();
      return new ServiceTemplate(model, beanType, gcl);
    } catch (Exception e) {
      if(this.rev != null) {
        ServiceAstType model = new ImmutableScriptModel("historic", rev, src.getKey(), src.getValue(), null, null);
        return new ServiceHistoric(model);
      }
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private Map.Entry<String, List<AstCommandType>> getSrc(String src, JsonNode jsonNode) {
    if(this.jsonNode != null) {
      return scriptParsers.parse(this.jsonNode, this.rev);
    }
    return scriptParsers.parse(this.src, this.rev);
  }

  protected ImmutableScriptMethodModel getMethods(Class<?> beanType) {
    int index = 0;
    List<ImmutableScriptMethodModel> result = new ArrayList<>();
    for(Method method : beanType.getDeclaredMethods()) {
      if(method.getName().equals("execute") && Modifier.isPublic(method.getModifiers()) && !Modifier.isVolatile(method.getModifiers())) {

        List<ServiceDataParamModel> params = getParams(method);
        Assert.isTrue(result.isEmpty(), () -> "Only one 'execute' method allowed!");
        result.add(new ImmutableScriptMethodModel(index++, method.getName(), Collections.unmodifiableList(params)));
      }
    }
    Assert.isTrue(result.size() == 1, () -> "There must be one 'execute' method!");
    return result.iterator().next();
  }

  protected String getSimpleName(ServiceDataParamModel model) {
    return model.getType().getBeanType().getSimpleName();
  }

  protected String getCanonicalName(ServiceDataParamModel model) {
    return model.getType().getBeanType().getCanonicalName();
  }

  protected Class<?> generateBeanType(GroovyClassLoader groovyClassLoader, String src) {
    return groovyClassLoader.parseClass(src);
  }

  protected List<ServiceDataParamModel> getParams(Method method) {
    List<ServiceDataParamModel> result = new ArrayList<>();
    int index = 0;
    for(Parameter parameter : method.getParameters()) {
      ServiceParamType contextType = getContextType(parameter.getType());
      DataTypeBuilder dataTypeBuilder = dataTypeRepository.create().
          name(parameter.getName()).
          direction(Direction.IN).
          beanType(parameter.getType()).
          valueType(ValueType.OBJECT);
      getWrenchFlowParameter(dataTypeBuilder, parameter.getType(), contextType, Direction.IN);
      result.add(new ImmutableScriptParameterModel(index++, dataTypeBuilder.build(), contextType));
    }

    index = 0;
    Class<?> returnType = method.getReturnType();
    ServiceParamType contextType = getContextType(returnType);
    if(contextType == ServiceParamType.INTERNAL) {
      Assert.isTrue(returnType == void.class, () -> "'execute' must be void or return type must define: " + ServiceData.class.getCanonicalName() + "!");
    } else {
      DataTypeBuilder dataTypeBuilder = dataTypeRepository.create().
          name(returnType.getSimpleName()).
          direction(Direction.OUT).
          beanType(returnType).
          valueType(ValueType.OBJECT);
      getWrenchFlowParameter(dataTypeBuilder, returnType, contextType, Direction.OUT);
      result.add(new ImmutableScriptParameterModel(index++, dataTypeBuilder.build(), contextType));
    }

    return result;
  }

  protected void getWrenchFlowParameter(DataTypeBuilder parentDataTypeBuilder, Class<?> type, ServiceParamType contextType, Direction direction) {
    if(contextType == ServiceParamType.INTERNAL) {
      return;
    }

    Assert.isTrue(Serializable.class.isAssignableFrom(type), () -> "Flow types must implement Serializable!");
    for(Field field : type.getDeclaredFields()) {
      int modifier = field.getModifiers();
      if( Modifier.isFinal(modifier) ||
          Modifier.isTransient(modifier) ||
          Modifier.isStatic(modifier) ||
          field.getName().startsWith("$") ||
          field.getName().startsWith("_")) {
        continue;
      }
      parentDataTypeBuilder.property().name(field.getName()).direction(direction).beanType(field.getType()).build();
    }
  }

  protected ServiceParamType getContextType(Class<?> type) {
    return type.isAnnotationPresent(ServiceData.class) ? ServiceParamType.EXTERNAL : ServiceParamType.INTERNAL;
  }
}
