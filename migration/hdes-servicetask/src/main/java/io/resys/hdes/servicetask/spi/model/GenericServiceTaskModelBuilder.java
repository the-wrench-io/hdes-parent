package io.resys.hdes.servicetask.spi.model;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Range;

import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.servicetask.api.ImmutableServiceTaskModel;
import io.resys.hdes.servicetask.api.ImmutableSource;
import io.resys.hdes.servicetask.api.ServiceTaskCommandType;
import io.resys.hdes.servicetask.api.ServiceTaskModel;
import io.resys.hdes.servicetask.api.ServiceTaskService;
import io.resys.hdes.servicetask.spi.model.beans.ServiceTaskSourceBean;

public class GenericServiceTaskModelBuilder implements ServiceTaskService.ModelBuilder {
  private final static String LINE_SEPARATOR = System.lineSeparator();

  private final List<DataTypeCommand> src = new ArrayList<>();
  private final ServiceTaskFactory serviceTaskFactory;

  private Integer rev;
  private Class<?> context;
  
  public GenericServiceTaskModelBuilder(ServiceTaskFactory serviceTaskFactory) {
    super();
    this.serviceTaskFactory = serviceTaskFactory;
  }

  @Override
  public ServiceTaskService.ModelBuilder src(List<DataTypeCommand> src) {
    Assert.notNull(src, () -> "src can't be null");
    this.src.addAll(src);
    return this;
  }

  @Override
  public ServiceTaskService.ModelBuilder rev(Integer version) {
    this.rev = version;
    return this;
  }
  
  @Override
  public ServiceTaskService.ModelBuilder context(Class<?> context) {
    this.context = context;
    return this;
  }

  @Override
  public ServiceTaskModel build() {
    List<ServiceTaskSourceBean> body = new ArrayList<>();
    List<ServiceTaskSourceBean> imported = new ArrayList<>();
    List<ServiceTaskSourceBean> input = new ArrayList<>();
    List<ServiceTaskSourceBean> output = new ArrayList<>();
    List<ServiceTaskSourceBean> staticBody = new ArrayList<>();
    String name = null;
    int index = 1;
    for (DataTypeCommand command : src) {
      if (rev != null && index++ > rev) {
        break;
      }
      if(ServiceTaskCommandType.valueOf(command.getType()) == ServiceTaskCommandType.SET_NAME) {
        name = command.getValue();
        continue;
      }
      
      if(command.getSubType() == null) {
        continue;
      }
      
      List<ServiceTaskSourceBean> target;
      switch (ServiceTaskCommandType.SubType.valueOf(command.getSubType()) ) {
      case BODY:
        target = body;
        break;
      case IMPORT:
        target = imported;
        break;
      case INPUT:
        target = input;
        break;
      case OUTPUT:
        target = output;
        break;
      case STATIC:
        target = staticBody;
        break;
      default:
        continue;
      }
      addCommand(command, target);
    }

    ImmutableSource src = ImmutableSource.builder()
      .imports(getSrc(imported))
      .executeBody(getSrc(body))
      .inputBody(getSrc(input))
      .outputBody(getSrc(output))
      .staticBody(getSrc(staticBody))
      .build();

    ServiceTaskFactory.SourceAndType sourceAndType = serviceTaskFactory.builder().context(context).name(name).src(src).build();
    return ImmutableServiceTaskModel.builder()
      .rev(index)
      .id(name)
      .src(ImmutableSource.builder().from(src).value(sourceAndType.getSrc()).build())
      .type(sourceAndType.getType())
      .build();
  }

  private String getSrc(List<ServiceTaskSourceBean> src) {
    StringBuilder result = new StringBuilder();
    src.stream().sorted().forEachOrdered(s -> result.append(s.getValue()).append(LINE_SEPARATOR));
    return result.toString();
  }
  
  private void addCommand(DataTypeCommand command, List<ServiceTaskSourceBean> added) {
    int line = command.getId();
    switch (ServiceTaskCommandType.valueOf(command.getType())) {
    case ADD:
      added.stream().filter(s -> s.getLine() >= line).forEach(s -> s.setLine(s.getLine() + 1));
      added.add(new ServiceTaskSourceBean(line, command));
      break;
    case DELETE:
      // Delete set of lines
      int from = command.getId();
      int to = StringUtils.isEmpty(command.getValue()) ? command.getId() : Integer.parseInt(command.getValue());
      Range<Integer> range = Range.closed(from, to);
      int linesToDelete = (to - from) + 1;
      Iterator<ServiceTaskSourceBean> sources = added.iterator();
      while (sources.hasNext()) {
        ServiceTaskSourceBean source = sources.next();
        if (range.contains(source.getLine())) {
          sources.remove();
        } else if (source.getLine() > to) {
          source.setLine(source.getLine() - linesToDelete);
        }
      }
      break;
    case SET:
      Optional<ServiceTaskSourceBean> source = added.stream().filter(s -> s.getLine() == line).findFirst();
      Assert.isTrue(source.isPresent(), () -> String.format("Can't change value of non existing line: %s!", line));
      source.get().add(command);
      break;
    default:
      break;
    }
  }
}
