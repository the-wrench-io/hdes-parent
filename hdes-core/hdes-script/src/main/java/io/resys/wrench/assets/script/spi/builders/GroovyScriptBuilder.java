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
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.JsonNode;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import groovy.lang.GroovyClassLoader;
import io.resys.wrench.assets.context.stereotypes.WrenchFlowParameter;
import io.resys.wrench.assets.datatype.api.DataTypeRepository;
import io.resys.wrench.assets.datatype.api.DataTypeRepository.DataTypeBuilder;
import io.resys.wrench.assets.datatype.api.DataTypeRepository.Direction;
import io.resys.wrench.assets.datatype.api.DataTypeRepository.ValueType;
import io.resys.wrench.assets.datatype.spi.util.Assert;
import io.resys.wrench.assets.script.api.ScriptRepository.Script;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptBuilder;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptCommand;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptConstructor;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptMethodModel;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptModel;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptModelBuilder;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptParameterContextType;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptParameterModel;
import io.resys.wrench.assets.script.spi.ScriptTemplate;
import io.resys.wrench.assets.script.spi.beans.ImmutableScriptMethodModel;
import io.resys.wrench.assets.script.spi.beans.ImmutableScriptModel;
import io.resys.wrench.assets.script.spi.beans.ImmutableScriptParameterModel;


public class GroovyScriptBuilder implements ScriptBuilder {
  private static final Charset UTF_8 = Charset.forName("utf-8");

  private final DataTypeRepository dataTypeRepository;
  private final Configuration cfg;
  private final Supplier<ScriptModelBuilder> modelBuilder;
  private final GroovyScriptParser scriptParsers;
  private final ScriptConstructor constructor;
  
  private String src;
  private Integer rev;
  private JsonNode jsonNode;

  public GroovyScriptBuilder(
      ScriptConstructor constructor,
      GroovyScriptParser scriptParsers,
      DataTypeRepository dataTypeRepository,
      Configuration cfg, Supplier<ScriptModelBuilder> modelBuilder) {
    super();
    this.constructor = constructor;
    this.dataTypeRepository = dataTypeRepository;
    this.cfg = cfg;
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
  public Script build() {
    Assert.isTrue(src != null || jsonNode != null, () -> "src can't be null!");

    final Map.Entry<String, List<ScriptCommand>> src = getSrc(this.src, this.jsonNode);
    final int rev = this.rev != null ? this.rev : src.getValue().size();
    final GroovyClassLoader gcl = new GroovyClassLoader();

    try {
      final Class<?> beanType = generateBeanType(gcl, src.getKey());
      final ImmutableScriptMethodModel method = getMethods(beanType);
      final Class<Script> executorType = generateExecutorType(gcl, method, beanType);
      final ScriptModel model = modelBuilder.get().src(src.getKey()).commands(src.getValue()).rev(rev).type(beanType).method(method).build();
      final Object bean = constructor.get(beanType);
      return executorType.getConstructor(beanType, ScriptModel.class).newInstance(bean, model);

    } catch (Exception e) {
      if(this.rev != null) {
        ScriptModel model = new ImmutableScriptModel("historic", rev, src.getKey(), src.getValue(), null, null);
        return new ScriptTemplate(model) {
          @Override
          public Object execute(List<Object> facts) {
            return null;
          }
        };
      }
      throw new RuntimeException(e.getMessage(), e);
    } finally {
      try {
        // TODO This do not work with groovy 2.5+. GroovyClassLoader may not be closed until script becomes unused.
        gcl.close();
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }
  }

  private Map.Entry<String, List<ScriptCommand>> getSrc(String src, JsonNode jsonNode) {
    if(this.jsonNode != null) {
      return scriptParsers.parse(this.jsonNode, this.rev);
    }
    return scriptParsers.parse(this.src, this.rev);
  }

  @SuppressWarnings("unchecked")
  protected Class<Script> generateExecutorType(GroovyClassLoader groovyClassLoader, ScriptMethodModel method, Class<?> beanType) {

    Set<String> imports = new HashSet<>();

    imports.add(ArrayList.class.getCanonicalName());
    imports.add(HashSet.class.getCanonicalName());
    imports.add(beanType.getCanonicalName());
    imports.add(ScriptMethodModel.class.getCanonicalName());
    imports.add(ScriptTemplate.class.getCanonicalName());
    imports.add(ScriptModel.class.getCanonicalName());

    for(ScriptParameterModel arg : method.getParameters()) {
      imports.add(arg.getType().getBeanType().getCanonicalName());
    }

    Map<String, Object> entity = new HashMap<>();
    entity.put("packageName", beanType.getPackage().getName());
    entity.put("name", beanType.getSimpleName() + "Executor");
    entity.put("imports", imports);
    entity.put("beanSimpleName", beanType.getCanonicalName());
    entity.put("executorMethod", method);
    entity.put("inputs", method.getParameters().stream().filter(p -> p.getType().getDirection() == Direction.IN).collect(Collectors.toList()));
    entity.put("helper", this);

    StringWriter writer = new StringWriter();
    try {
      cfg.getTemplate("ftl/executor.ftl").process(entity, writer);
      writer.flush();
      writer.close();
    } catch(TemplateException | IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    return groovyClassLoader.parseClass(writer.toString());
  }

  protected ImmutableScriptMethodModel getMethods(Class<?> beanType) {
    int index = 0;
    List<ImmutableScriptMethodModel> result = new ArrayList<>();
    for(Method method : beanType.getDeclaredMethods()) {
      if(method.getName().equals("execute") && Modifier.isPublic(method.getModifiers()) && !Modifier.isVolatile(method.getModifiers())) {

        List<ScriptParameterModel> params = getParams(method);
        Assert.isTrue(result.isEmpty(), () -> "Only one 'execute' method allowed!");
        result.add(new ImmutableScriptMethodModel(String.valueOf(index++), method.getName(), Collections.unmodifiableList(params)));
      }
    }
    Assert.isTrue(result.size() == 1, () -> "There must be one 'execute' method!");
    return result.iterator().next();
  }

  protected String getSimpleName(ScriptParameterModel model) {
    return model.getType().getBeanType().getSimpleName();
  }

  protected String getCanonicalName(ScriptParameterModel model) {
    return model.getType().getBeanType().getCanonicalName();
  }

  protected Class<?> generateBeanType(GroovyClassLoader groovyClassLoader, String src) {
    return groovyClassLoader.parseClass(src);
  }

  protected List<ScriptParameterModel> getParams(Method method) {
    List<ScriptParameterModel> result = new ArrayList<>();
    for(Parameter parameter : method.getParameters()) {
      ScriptParameterContextType contextType = getContextType(parameter.getType());
      DataTypeBuilder dataTypeBuilder = dataTypeRepository.createBuilder().
          name(parameter.getName()).
          direction(Direction.IN).
          beanType(parameter.getType()).
          valueType(ValueType.OBJECT);
      getWrenchFlowParameter(dataTypeBuilder, parameter.getType(), contextType, Direction.IN);
      result.add(new ImmutableScriptParameterModel(dataTypeBuilder.build(), contextType));
    }

    Class<?> returnType = method.getReturnType();
    ScriptParameterContextType contextType = getContextType(returnType);
    if(contextType == ScriptParameterContextType.INTERNAL) {
      Assert.isTrue(returnType == void.class, () -> "'execute' must be void or return type must define: " + WrenchFlowParameter.class.getCanonicalName() + "!");
    } else {
      DataTypeBuilder dataTypeBuilder = dataTypeRepository.createBuilder().
          name(returnType.getSimpleName()).
          direction(Direction.OUT).
          beanType(returnType).
          valueType(ValueType.OBJECT);
      getWrenchFlowParameter(dataTypeBuilder, returnType, contextType, Direction.OUT);
      result.add(new ImmutableScriptParameterModel(dataTypeBuilder.build(), contextType));
    }

    return result;
  }

  protected void getWrenchFlowParameter(DataTypeBuilder parentDataTypeBuilder, Class<?> type, ScriptParameterContextType contextType, Direction direction) {
    if(contextType == ScriptParameterContextType.INTERNAL) {
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

  protected ScriptParameterContextType getContextType(Class<?> type) {
    return type.isAnnotationPresent(WrenchFlowParameter.class) ? ScriptParameterContextType.EXTERNAL : ScriptParameterContextType.INTERNAL;
  }
}
