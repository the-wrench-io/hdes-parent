package io.resys.hdes.servicetask.spi.model.groovy;

/*-
 * #%L
 * hdes-servicetask
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
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import freemarker.template.TemplateException;
import groovy.lang.GroovyClassLoader;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.servicetask.api.ServiceTask;
import io.resys.hdes.servicetask.api.ServiceTaskModel;
import io.resys.hdes.servicetask.spi.model.ImmutableSourceAndType;
import io.resys.hdes.servicetask.spi.model.ServiceTaskFactory;
import io.resys.hdes.servicetask.spi.model.ServiceTaskFactory.ServiceTaskBuilder;
import io.resys.hdes.servicetask.spi.model.ServiceTaskFactory.SourceAndType;

public class GroovyServiceTaskBuilder implements ServiceTaskFactory.ServiceTaskBuilder {
  private static final freemarker.template.Template TEMPLATE;
  private static final GroovyClassLoader GROOVY_CLASS_LOADER = new GroovyClassLoader();
  private static final String PKG_NAME = GroovyServiceTaskBuilder.class.getPackage().getName();
  static {
    freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_22);
    cfg.setClassForTemplateLoading(GroovyServiceTaskBuilder.class, "/");
    try {
      TEMPLATE = cfg.getTemplate("ftl/executor.ftl");
    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  };
  
  private Class<?> context;
  private String name;
  private ServiceTaskModel.Source src;

  @Override
  public ServiceTaskBuilder src(ServiceTaskModel.Source src) {
    this.src = src;
    return this;
  }
  
  @Override
  public ServiceTaskBuilder name(String name) {
    this.name = name;
    return this;
  }

  @Override
  public ServiceTaskBuilder context(Class<?> context) {
    this.context = context;
    return this;
  }

  @SuppressWarnings({ "unchecked" })
  @Override
  public SourceAndType build() {
    Assert.notNull(src, () -> "src can't be null!");
    Assert.notNull(name, () -> "name can't be null!");
    
    StringWriter writer = new StringWriter();
    try {
      Map<String, Object> inputs = createInputs(src, context);
      TEMPLATE.process(inputs, writer);
      writer.flush();
      writer.close();
    } catch (TemplateException | IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    String groovySource = writer.toString();
    Class<ServiceTask<?, ?, ?>> groovyClass = GROOVY_CLASS_LOADER.parseClass(groovySource);   
    return ImmutableSourceAndType.builder()
        .type(groovyClass)
        .src(groovySource)
        .build();
  }

  private Map<String, Object> createInputs(ServiceTaskModel.Source src, Class<?> context) {
    Class<?> contextType = context == null ? Object.class : context;
    
    Set<String> imports = new HashSet<>();
    imports.add(contextType.getCanonicalName());
    
    Map<String, Object> entity = new HashMap<>();
    entity.put("packageName", PKG_NAME);
    entity.put("imports", imports);
    entity.put("src", src);
    entity.put("contextType", contextType.getName());
    entity.put("name", name);
    return entity;
  }
}
