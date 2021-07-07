package io.resys.wrench.assets.flow.spi.expressions;

/*-
 * #%L
 * wrench-component-assets-flow
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

import java.util.Map;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;

@SuppressWarnings({"unchecked", "rawtypes"})
public class MapPropertyAccessor implements PropertyAccessor {

  @Override
  public Class<?>[] getSpecificTargetClasses() {
    return new Class<?>[] { Map.class };
  }

  @Override
  public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
    return target instanceof Map;
  }

  @Override
  public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
     Object object = ((Map) target).get(name);
     return new TypedValue(object);
  }

  @Override
  public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
    return target instanceof Map;
  }

  @Override
  public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
    ((Map) target).put(name, newValue);
  }
}
