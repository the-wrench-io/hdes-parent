package io.resys.wrench.assets.script.spi.builders;

/*-
 * #%L
 * wrench-assets-script
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Range;

import io.resys.wrench.assets.datatype.spi.util.Assert;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptCommandType;
import io.resys.wrench.assets.script.spi.beans.ScriptSourceBean;
import io.resys.wrench.assets.script.spi.beans.ScriptSourceCommandBean;

public class GrooovyCommandBuilder {
  private final static String LINE_SEPARATOR = "\r\n";

  private final List<ScriptSourceBean> sourcesAdded = new ArrayList<>();
  private final List<ScriptSourceBean> sourcesDeleted = new ArrayList<>();

  public GrooovyCommandBuilder add(ScriptSourceCommandBean command) {
    int line = command.getId();
    if(command.getType() == ScriptCommandType.DELETE) {
      delete(line, Integer.parseInt(command.getValue()));
    } else if(command.getType() == ScriptCommandType.ADD) {
      add(line, command.getValue());
    } else {
      set(line, command.getValue());
    }
    return this;
  }

  private GrooovyCommandBuilder add(int line, String value) {
    sourcesAdded.stream().filter(s -> s.getLine() >= line).forEach(s -> s.setLine(s.getLine() + 1));
    sourcesAdded.add(new ScriptSourceBean(line, new ScriptSourceCommandBean(line, value, ScriptCommandType.ADD)));
    return this;
  }

  private GrooovyCommandBuilder set(int line, String value) {
    Optional<ScriptSourceBean> source = sourcesAdded.stream().filter(s -> s.getLine() == line).findFirst();
    Assert.isTrue(source.isPresent(), () -> String.format("Can't change value of non existing line: %s!", line));
    source.get().add(new ScriptSourceCommandBean(line, value, ScriptCommandType.SET));
    return this;
  }

  private GrooovyCommandBuilder delete(int from, int to) {
    Range<Integer> range = Range.closed(from, to);
    int linesToDelete = (to - from) + 1;

    Iterator<ScriptSourceBean> sources = sourcesAdded.iterator();
    while(sources.hasNext()) {
      ScriptSourceBean source = sources.next();

      if(range.contains(source.getLine())) {
        sources.remove();
        sourcesDeleted.add(source);
      } else if(source.getLine() > to) {
        source.setLine(source.getLine() - linesToDelete);
      }
    }

    return this;
  }

  public String build() {
    Collections.sort(sourcesAdded);
    Iterator<ScriptSourceBean> iterator = sourcesAdded.iterator();
    StringBuilder value = new StringBuilder();

    while(iterator.hasNext()) {
      ScriptSourceBean src = iterator.next();
      String lineContent = src.getCommands().get(src.getCommands().size() - 1).getValue();

      if(!StringUtils.isEmpty(lineContent)) {
        value.append(lineContent);
      }
      value.append(LINE_SEPARATOR);
    }

    return buildSource(value);
  }
  
  private String buildSource(StringBuilder value) {
    String result = value.toString();
    if(result.endsWith(LINE_SEPARATOR)) {
      return result.substring(0, result.length() - LINE_SEPARATOR.length());
    }
    return result;
  }
}
